package com.falcon.hydrohabit.tracking

import com.falcon.hydrohabit.features.history.model.IntakeEntry
import com.falcon.hydrohabit.features.history.source.IntakeLogRepository
import com.falcon.hydrohabit.features.history.usecase.perksForStreak
import com.falcon.hydrohabit.features.homescreen.utils.WaterAmount
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

/**
 * The single place that logs and removes drinks. The home screen, History, Siri
 * shortcuts and Apple Health all go through it, so today's total, the intake log,
 * the streak and Health always agree.
 */
class HydrationTracker(
    private val onboardingRepo: OnboardingRepositoryContract,
    private val intakeLog: IntakeLogRepository,
    private val healthSync: HealthSync,
    /** Whether the user turned on "Save drinks to Health". */
    private val isHealthSyncEnabled: suspend () -> Boolean,
    private val now: () -> Instant = { Clock.System.now() },
    private val timeZone: () -> TimeZone = { TimeZone.currentSystemDefault() },
) {
    private val mutex = Mutex()

    data class Change(
        val entry: IntakeEntry,
        /** Today's total after the change. */
        val totalMl: Int,
        val goalMl: Int,
        /** True when this drink took today from below the goal to the goal or above. */
        val reachedGoal: Boolean = false,
    )

    private fun localNow(): LocalDateTime = now().toLocalDateTime(timeZone())

    private suspend fun goalFor(water: WaterAmount): Int =
        water.onTotalWater.takeIf { it > 0 } ?: onboardingRepo.getUserSettingsStore().first().userWaterIntake

    /** Logs a drink of [amountMl] at the current time. */
    suspend fun addWater(amountMl: Int): Change {
        require(amountMl > 0) { "amountMl must be positive" }
        val instant = now()
        val entry = IntakeEntry(
            id = newId(instant),
            epochMillis = instant.toEpochMilliseconds(),
            date = instant.toLocalDateTime(timeZone()).date.toString(),
            amountMl = amountMl,
        )
        return insert(entry)
    }

    /** Puts back an entry that was removed (Undo). Keeps its original time. */
    suspend fun restore(entry: IntakeEntry): Change = insert(entry.copy(healthId = null))

    private suspend fun insert(entry: IntakeEntry): Change {
        val change = mutex.withLock {
            val local = localNow()
            val today = local.date.toString()
            val water = onboardingRepo.getWaterAmount().first()
            val goal = goalFor(water)
            val before = if (water.onWaterDay == today) water.onUsedWater else 0

            // Only today's goal is known here; a restored past drink keeps that day's recorded goal.
            intakeLog.add(entry, if (entry.date == today) goal else 0)
            if (entry.date != today) {
                return@withLock Change(entry, before, goal)
            }
            val after = before + entry.amountMl
            onboardingRepo.updateWaterAmount(after, goal, today)
            val reached = goal > 0 && before < goal && after >= goal
            if (reached) creditStreak(today, local.date.dayOfMonth)
            Change(entry, after, goal, reached)
        }
        val healthId = saveToHealth(change.entry)
        return change.copy(entry = change.entry.copy(healthId = healthId))
    }

    /** Deletes a logged drink. Returns what was removed, or null if it was already gone. */
    suspend fun remove(entryId: String): Change? {
        val change = mutex.withLock {
            val removed = intakeLog.remove(entryId) ?: return@withLock null
            val local = localNow()
            val today = local.date.toString()
            val water = onboardingRepo.getWaterAmount().first()
            val goal = goalFor(water)
            if (removed.date != today || water.onWaterDay != today) {
                return@withLock Change(removed, if (water.onWaterDay == today) water.onUsedWater else 0, goal)
            }
            val before = water.onUsedWater
            val after = (before - removed.amountMl).coerceAtLeast(0)
            onboardingRepo.updateWaterAmount(after, goal, today)
            if (goal > 0 && before >= goal && after < goal) revokeStreak(today, local.date.dayOfMonth)
            Change(removed, after, goal)
        } ?: return null
        change.entry.healthId?.let { runCatching { healthSync.deleteWater(it) } }
        return change
    }

    /**
     * Today's total can include water logged before the intake log existed (older app
     * versions). Adds one entry for the difference so History matches the home screen.
     */
    suspend fun reconcileToday() {
        mutex.withLock {
            val today = localNow().date.toString()
            val water = onboardingRepo.getWaterAmount().first()
            if (water.onWaterDay != today || water.onUsedWater <= 0) return
            val logged = intakeLog.current().entries.filter { it.date == today }.sumOf { it.amountMl }
            val missing = water.onUsedWater - logged
            if (missing > 0) {
                val instant = now()
                intakeLog.add(
                    IntakeEntry(newId(instant), instant.toEpochMilliseconds(), today, missing),
                    goalFor(water),
                )
            } else {
                intakeLog.recordGoal(today, goalFor(water))
            }
        }
    }

    /** After the user turns on Apple Health, sends today's drinks that aren't there yet. */
    suspend fun syncTodayToHealth() {
        val today = localNow().date.toString()
        intakeLog.current().entries
            .filter { it.date == today && it.healthId == null }
            .forEach { saveToHealth(it) }
    }

    private suspend fun saveToHealth(entry: IntakeEntry): String? {
        if (!healthSync.isAvailable || !isHealthSyncEnabled()) return null
        val id = runCatching { healthSync.saveWater(entry.amountMl, entry.epochMillis) }.getOrNull() ?: return null
        intakeLog.setHealthId(entry.id, id)
        return id
    }

    private suspend fun creditStreak(today: String, dayOfMonth: Int) {
        val s = onboardingRepo.getStreak().first()
        if (s.streakDay == today) return
        val streak = s.streak + 1
        onboardingRepo.updateStreak(
            streak = streak,
            streakDays = (s.streakDays + (dayOfMonth - 1)).distinct(),
            streakDay = today,
            waterTime = s.waterTime,
            perks = perksForStreak(streak),
        )
    }

    private suspend fun revokeStreak(today: String, dayOfMonth: Int) {
        val s = onboardingRepo.getStreak().first()
        if (s.streakDay != today) return
        val streak = (s.streak - 1).coerceAtLeast(0)
        onboardingRepo.updateStreak(
            streak = streak,
            streakDays = s.streakDays - (dayOfMonth - 1),
            streakDay = "",
            waterTime = s.waterTime,
            perks = perksForStreak(streak),
        )
    }

    private fun newId(instant: Instant): String =
        instant.toEpochMilliseconds().toString(36) + "-" + Random.nextLong().toULong().toString(36)
}
