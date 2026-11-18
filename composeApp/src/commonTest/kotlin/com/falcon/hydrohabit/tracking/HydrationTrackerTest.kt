package com.falcon.hydrohabit.tracking

import com.falcon.hydrohabit.features.history.source.IntakeLogRepository
import com.falcon.hydrohabit.features.homescreen.utils.StreakClass
import com.falcon.hydrohabit.features.homescreen.utils.StreakMonthClass
import com.falcon.hydrohabit.features.homescreen.utils.UserSettings
import com.falcon.hydrohabit.features.homescreen.utils.UserValues
import com.falcon.hydrohabit.features.homescreen.utils.WaterAmount
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class FakeOnboardingRepo(goal: Int, water: WaterAmount, streak: StreakClass = StreakClass()) : OnboardingRepositoryContract {
    val streak = MutableStateFlow(streak)
    val water = MutableStateFlow(water)
    val settings = MutableStateFlow(UserSettings(userWaterIntake = goal, registrationCompleted = true))

    override suspend fun updateStreak(streak: Int, streakDays: List<Int>, streakDay: String, waterTime: String, perks: List<Int>) {
        this.streak.value = StreakClass(streak, streakDay, streakDays, waterTime, perks)
    }
    override fun getStreak(): Flow<StreakClass> = streak
    override suspend fun updateStreakMonth(streakDay: Int, streakMonth: Int, streakYear: Int) = Unit
    override fun getStreakMonth(): Flow<StreakMonthClass> = MutableStateFlow(StreakMonthClass())
    override suspend fun updateUserSettingsStore(userHeight: Int, userWaterIntake: Int, userName: String, userWeight: Int, onBoardingCompleted: Boolean) = Unit
    override fun getUserSettingsStore(): Flow<UserSettings> = settings
    override suspend fun updateWaterAmount(onUsedWater: Int, onTotalWaterAmount: Int, onWaterDay: String) {
        water.value = WaterAmount(onUsedWater, onTotalWaterAmount, onWaterDay)
    }
    override fun removeWaterAmount() = Unit
    override fun getWaterAmount(): Flow<WaterAmount> = water
    override suspend fun updateUserValues(avgIntake: String, bestStreak: String) = Unit
    override fun getUserValues(): Flow<UserValues> = MutableStateFlow(UserValues())
}

private class FakeHealth(var granted: Boolean = true) : HealthSync {
    val saved = mutableMapOf<String, Int>()
    private var next = 0
    override val isAvailable = true
    override val providerName = "Apple Health"
    override suspend fun requestAccess() = granted
    override suspend fun saveWater(amountMl: Int, epochMillis: Long): String? {
        if (!granted) return null
        val id = "hk-${next++}"
        saved[id] = amountMl
        return id
    }
    override suspend fun deleteWater(healthId: String) { saved.remove(healthId) }
}

class HydrationTrackerTest {
    private val zone = TimeZone.UTC
    private var clock: Instant = LocalDateTime(2026, 11, 18, 9, 0).toInstant(zone)
    private val today = "2026-11-18"

    private fun newLog(): IntakeLogRepository {
        val dir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "droply-test-${Random.nextLong().toULong()}"
        FileSystem.SYSTEM.createDirectories(dir)
        return IntakeLogRepository { dir / "intake_log.json" }
    }

    private fun tracker(
        repo: FakeOnboardingRepo,
        log: IntakeLogRepository = newLog(),
        health: HealthSync = NoHealthSync,
        healthOn: Boolean = false,
    ) = HydrationTracker(repo, log, health, { healthOn }, now = { clock }, timeZone = { zone })

    @Test
    fun addingWaterUpdatesTodayAndTheLog() = runTest {
        val repo = FakeOnboardingRepo(2000, WaterAmount(500, 2000, today))
        val log = newLog()
        val change = tracker(repo, log).addWater(250)
        assertEquals(750, change.totalMl)
        assertEquals(750, repo.water.value.onUsedWater)
        assertEquals(listOf(250), log.current().entries.map { it.amountMl })
        assertEquals(2000, log.current().goals[today])
        assertFalse(change.reachedGoal)
    }

    @Test
    fun firstDrinkOfANewDayStartsFromZero() = runTest {
        val repo = FakeOnboardingRepo(2000, WaterAmount(1800, 2000, "2026-11-17"))
        assertEquals(300, tracker(repo).addWater(300).totalMl)
    }

    @Test
    fun crossingTheGoalCelebratesAndCreditsTheStreakOnce() = runTest {
        val repo = FakeOnboardingRepo(2000, WaterAmount(1800, 2000, today), StreakClass(streak = 6, streakDay = "2026-11-17"))
        val t = tracker(repo)
        assertTrue(t.addWater(250).reachedGoal)
        assertEquals(7, repo.streak.value.streak)
        assertEquals(today, repo.streak.value.streakDay)
        assertEquals(listOf(1, 3), repo.streak.value.perks)

        // Drinking more after the goal keeps counting but doesn't celebrate or credit again.
        val more = t.addWater(500)
        assertFalse(more.reachedGoal)
        assertEquals(2550, more.totalMl)
        assertEquals(7, repo.streak.value.streak)
    }

    @Test
    fun undoBelowTheGoalTakesTheStreakBack() = runTest {
        val repo = FakeOnboardingRepo(2000, WaterAmount(1800, 2000, today), StreakClass(streak = 2, streakDay = "2026-11-17"))
        val t = tracker(repo)
        val added = t.addWater(250)
        assertEquals(3, repo.streak.value.streak)

        val removed = t.remove(added.entry.id)
        assertNotNull(removed)
        assertEquals(1800, removed.totalMl)
        assertEquals(2, repo.streak.value.streak)

        // Re-logging it reaches the goal again and credits it again.
        assertTrue(t.restore(added.entry).reachedGoal)
        assertEquals(3, repo.streak.value.streak)
        assertEquals(2050, repo.water.value.onUsedWater)
    }

    @Test
    fun restoreKeepsTheOriginalIdAndTime() = runTest {
        val repo = FakeOnboardingRepo(2000, WaterAmount(0, 2000, today))
        val log = newLog()
        val t = tracker(repo, log)
        val added = t.addWater(200)
        t.remove(added.entry.id)
        assertTrue(log.current().entries.isEmpty())
        clock = clock.plus(kotlin.time.Duration.parse("10m"))
        t.restore(added.entry)
        val back = log.current().entries.single()
        assertEquals(added.entry.id, back.id)
        assertEquals(added.entry.epochMillis, back.epochMillis)
    }

    @Test
    fun deletingAPastDrinkLeavesTodayAlone() = runTest {
        val repo = FakeOnboardingRepo(2000, WaterAmount(0, 2000, "2026-11-17"))
        val log = newLog()
        clock = LocalDateTime(2026, 11, 17, 20, 0).toInstant(zone)
        val t = tracker(repo, log)
        val yesterday = t.addWater(400)
        clock = LocalDateTime(2026, 11, 18, 8, 0).toInstant(zone)
        t.addWater(300)

        val removed = t.remove(yesterday.entry.id)
        assertNotNull(removed)
        assertEquals(300, repo.water.value.onUsedWater)
        assertEquals(listOf(today), log.current().entries.map { it.date })
    }

    @Test
    fun removingAMissingEntryReturnsNull() = runTest {
        assertNull(tracker(FakeOnboardingRepo(2000, WaterAmount(0, 2000, today))).remove("nope"))
    }

    @Test
    fun healthGetsEachDrinkAndLosesDeletedOnes() = runTest {
        val repo = FakeOnboardingRepo(2000, WaterAmount(0, 2000, today))
        val log = newLog()
        val health = FakeHealth()
        val t = tracker(repo, log, health, healthOn = true)
        val a = t.addWater(250)
        t.addWater(300)
        assertEquals(setOf(250, 300), health.saved.values.toSet())
        assertNotNull(log.current().entries.first { it.id == a.entry.id }.healthId)

        t.remove(a.entry.id)
        assertEquals(listOf(300), health.saved.values.toList())
    }

    @Test
    fun healthIsUntouchedWhenSyncIsOff() = runTest {
        val health = FakeHealth()
        tracker(FakeOnboardingRepo(2000, WaterAmount(0, 2000, today)), health = health, healthOn = false).addWater(250)
        assertTrue(health.saved.isEmpty())
    }

    @Test
    fun turningHealthOnSendsTodaysEarlierDrinks() = runTest {
        val repo = FakeOnboardingRepo(2000, WaterAmount(0, 2000, today))
        val log = newLog()
        val health = FakeHealth()
        var on = false
        val t = HydrationTracker(repo, log, health, { on }, now = { clock }, timeZone = { zone })
        t.addWater(250)
        t.addWater(400)
        assertTrue(health.saved.isEmpty())
        on = true
        t.syncTodayToHealth()
        assertEquals(setOf(250, 400), health.saved.values.toSet())
        t.syncTodayToHealth()
        assertEquals(2, health.saved.size, "already-synced drinks must not be sent twice")
    }

    @Test
    fun reconcileAddsWaterLoggedBeforeTheLogExistedOnce() = runTest {
        val repo = FakeOnboardingRepo(2000, WaterAmount(900, 2000, today))
        val log = newLog()
        val t = tracker(repo, log)
        t.reconcileToday()
        t.reconcileToday()
        assertEquals(listOf(900), log.current().entries.map { it.amountMl })
        assertEquals(900, repo.water.value.onUsedWater)
    }

    @Test
    fun goalFallsBackToTheUserSettingWhenTodayHasNone() = runTest {
        val repo = FakeOnboardingRepo(2400, WaterAmount(0, 0, ""))
        assertEquals(2400, tracker(repo).addWater(100).goalMl)
    }
}
