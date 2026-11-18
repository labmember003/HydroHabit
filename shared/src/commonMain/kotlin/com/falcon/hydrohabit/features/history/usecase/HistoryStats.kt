package com.falcon.hydrohabit.features.history.usecase

import com.falcon.hydrohabit.features.history.model.IntakeEntry
import com.falcon.hydrohabit.features.history.model.IntakeLog
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/** Monthly numbers shown at the top of the History screen. */
data class MonthSummary(
    /** Days in the month (up to today) on which the goal was reached. */
    val daysGoalMet: Int,
    /** Days of the month that have passed, including today. 0 for future months. */
    val daysElapsed: Int,
    /** Average intake over the days that have at least one drink logged. */
    val averageMl: Int,
    val totalMl: Int,
    val daysLogged: Int,
)

/** Pure history calculations, shared by Android and iOS and covered by unit tests. */
object HistoryStats {

    fun dailyTotals(log: IntakeLog): Map<LocalDate, Int> =
        log.entries
            .groupBy { it.date }
            .mapNotNull { (date, entries) ->
                runCatching { LocalDate.parse(date) }.getOrNull()?.let { it to entries.sumOf(IntakeEntry::amountMl) }
            }
            .toMap()

    /**
     * Goal for [date]: the goal recorded that day, else the most recent earlier goal,
     * else [fallbackGoal] (the user's current goal).
     */
    fun goalOn(log: IntakeLog, date: LocalDate, fallbackGoal: Int): Int {
        log.goals[date.toString()]?.let { if (it > 0) return it }
        val earlier = log.goals
            .mapNotNull { (d, goal) -> runCatching { LocalDate.parse(d) }.getOrNull()?.let { it to goal } }
            .filter { (d, goal) -> d < date && goal > 0 }
            .maxByOrNull { it.first }
        return earlier?.second ?: fallbackGoal
    }

    fun progressPercent(totalMl: Int, goalMl: Int): Int =
        if (goalMl <= 0 || totalMl <= 0) 0 else (totalMl.toLong() * 100 / goalMl).toInt()

    private fun metOn(log: IntakeLog, totals: Map<LocalDate, Int>, date: LocalDate, fallbackGoal: Int): Boolean {
        val goal = goalOn(log, date, fallbackGoal)
        return goal > 0 && (totals[date] ?: 0) >= goal
    }

    /**
     * Consecutive days the goal was met, ending today if today is already met,
     * otherwise ending yesterday (today still has time).
     */
    fun currentStreak(log: IntakeLog, today: LocalDate, fallbackGoal: Int): Int {
        val totals = dailyTotals(log)
        var day = if (metOn(log, totals, today, fallbackGoal)) today else today.minus(1, DateTimeUnit.DAY)
        var streak = 0
        while (metOn(log, totals, day, fallbackGoal)) {
            streak++
            day = day.minus(1, DateTimeUnit.DAY)
        }
        return streak
    }

    fun bestStreak(log: IntakeLog, fallbackGoal: Int): Int {
        val totals = dailyTotals(log)
        val metDays = totals.keys.filter { metOn(log, totals, it, fallbackGoal) }.sorted()
        var best = 0
        var run = 0
        var previous: LocalDate? = null
        for (day in metDays) {
            run = if (previous != null && previous.plus(1, DateTimeUnit.DAY) == day) run + 1 else 1
            best = maxOf(best, run)
            previous = day
        }
        return best
    }

    fun daysInMonth(year: Int, monthNumber: Int): Int {
        val first = LocalDate(year, monthNumber, 1)
        return first.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).dayOfMonth
    }

    /** The first day the user used the app (earliest drink or recorded goal), or null if none. */
    fun firstDay(log: IntakeLog): LocalDate? =
        (log.goals.keys + log.entries.map { it.date })
            .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }
            .minOrNull()

    /**
     * Days before the user's first day don't count as missed: a new user who starts on
     * the 18th sees "0 of 1 day", not "0 of 18 days".
     */
    fun monthSummary(log: IntakeLog, year: Int, monthNumber: Int, today: LocalDate, fallbackGoal: Int): MonthSummary {
        val totals = dailyTotals(log)
        val length = daysInMonth(year, monthNumber)
        val start = firstDay(log) ?: today
        val days = (1..length).map { LocalDate(year, monthNumber, it) }.filter { it <= today && it >= start }
        val logged = days.mapNotNull { day -> totals[day]?.takeIf { it > 0 } }
        val total = logged.sum()
        return MonthSummary(
            daysGoalMet = days.count { metOn(log, totals, it, fallbackGoal) },
            daysElapsed = days.size,
            averageMl = if (logged.isEmpty()) 0 else total / logged.size,
            totalMl = total,
            daysLogged = logged.size,
        )
    }

    /** The last [count] days ending with [today], oldest first, with each day's total. */
    fun lastDays(log: IntakeLog, today: LocalDate, count: Int): List<Pair<LocalDate, Int>> {
        val totals = dailyTotals(log)
        return (count - 1 downTo 0).map { back ->
            val day = today.minus(back, DateTimeUnit.DAY)
            day to (totals[day] ?: 0)
        }
    }

    fun entriesOn(log: IntakeLog, date: LocalDate): List<IntakeEntry> =
        log.entries.filter { it.date == date.toString() }.sortedByDescending { it.epochMillis }
}

/** Achievement IDs earned for a streak length (see the achievements sheet). */
fun perksForStreak(streak: Int): List<Int> = buildList {
    if (streak >= 1) add(1)
    if (streak >= 7) add(3)
    if (streak >= 14) add(4)
    if (streak >= 30) add(2)
}
