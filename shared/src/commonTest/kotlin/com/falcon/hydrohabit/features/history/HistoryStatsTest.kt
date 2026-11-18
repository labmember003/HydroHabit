package com.falcon.hydrohabit.features.history

import com.falcon.hydrohabit.features.history.model.IntakeEntry
import com.falcon.hydrohabit.features.history.model.IntakeLog
import com.falcon.hydrohabit.features.history.usecase.HistoryStats
import com.falcon.hydrohabit.features.history.usecase.perksForStreak
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class HistoryStatsTest {

    private var counter = 0L
    private fun entry(date: String, ml: Int) = IntakeEntry("id${counter++}", counter, date, ml)

    private fun logOf(vararg days: Pair<String, Int>, goals: Map<String, Int> = emptyMap()) =
        IntakeLog(entries = days.map { (d, ml) -> entry(d, ml) }, goals = goals)

    private val today = LocalDate(2026, 11, 18)

    @Test
    fun dailyTotalsSumEachDay() {
        val log = logOf("2026-11-18" to 250, "2026-11-18" to 500, "2026-11-17" to 1000)
        assertEquals(mapOf(LocalDate(2026, 11, 18) to 750, LocalDate(2026, 11, 17) to 1000), HistoryStats.dailyTotals(log))
    }

    @Test
    fun goalUsesThatDayThenEarlierThenCurrent() {
        val log = IntakeLog(goals = mapOf("2026-11-10" to 2000, "2026-11-15" to 2500))
        assertEquals(2500, HistoryStats.goalOn(log, LocalDate(2026, 11, 15), 3000))
        assertEquals(2000, HistoryStats.goalOn(log, LocalDate(2026, 11, 12), 3000))
        assertEquals(3000, HistoryStats.goalOn(log, LocalDate(2026, 11, 1), 3000))
    }

    @Test
    fun currentStreakCountsTodayWhenMet() {
        val log = logOf("2026-11-16" to 2000, "2026-11-17" to 2000, "2026-11-18" to 2000)
        assertEquals(3, HistoryStats.currentStreak(log, today, 2000))
    }

    @Test
    fun currentStreakEndsYesterdayWhileTodayIsInProgress() {
        val log = logOf("2026-11-16" to 2000, "2026-11-17" to 2100, "2026-11-18" to 300)
        assertEquals(2, HistoryStats.currentStreak(log, today, 2000))
    }

    @Test
    fun missedDayBreaksTheStreak() {
        val log = logOf("2026-11-15" to 2000, "2026-11-17" to 2000)
        assertEquals(1, HistoryStats.currentStreak(log, today, 2000))
    }

    @Test
    fun bestStreakFindsLongestRun() {
        val log = logOf(
            "2026-10-30" to 2000, "2026-10-31" to 2000, "2026-11-01" to 2000, // 3 across a month boundary
            "2026-11-05" to 2000,
            "2026-11-10" to 2000, "2026-11-11" to 500,
        )
        assertEquals(3, HistoryStats.bestStreak(log, 2000))
    }

    @Test
    fun goalChangesAreRespectedPerDay() {
        // 2,200 ml met the old 2,000 ml goal but not the new 2,600 ml one.
        val log = logOf("2026-11-16" to 2200, "2026-11-17" to 2200, goals = mapOf("2026-11-16" to 2000, "2026-11-17" to 2600))
        assertEquals(1, HistoryStats.bestStreak(log, 2600))
    }

    @Test
    fun monthSummaryCountsElapsedDaysAndAveragesLoggedDays() {
        val log = logOf("2026-11-01" to 2000, "2026-11-02" to 1000, "2026-11-18" to 3000, "2026-10-31" to 5000)
        val s = HistoryStats.monthSummary(log, 2026, 11, today, 2000)
        assertEquals(18, s.daysElapsed)
        assertEquals(2, s.daysGoalMet)
        assertEquals(3, s.daysLogged)
        assertEquals(2000, s.averageMl)
        assertEquals(6000, s.totalMl)
    }

    @Test
    fun pastMonthSummaryCoversWholeMonth() {
        val s = HistoryStats.monthSummary(logOf("2026-01-15" to 2000), 2026, 2, today, 2000)
        assertEquals(28, s.daysElapsed)
        assertEquals(0, s.averageMl)
    }

    @Test
    fun daysBeforeFirstUseDontCountAsMissed() {
        val newUser = HistoryStats.monthSummary(IntakeLog(), 2026, 11, today, 2000)
        assertEquals(1, newUser.daysElapsed)
        assertEquals(0, newUser.daysGoalMet)
        val startedOn15th = HistoryStats.monthSummary(logOf("2026-11-15" to 2000), 2026, 11, today, 2000)
        assertEquals(4, startedOn15th.daysElapsed)
        assertEquals(0, HistoryStats.monthSummary(logOf("2026-11-15" to 2000), 2026, 10, today, 2000).daysElapsed)
    }

    @Test
    fun futureMonthHasNoElapsedDays() {
        assertEquals(0, HistoryStats.monthSummary(IntakeLog(), 2026, 12, today, 2000).daysElapsed)
    }

    @Test
    fun lastSevenDaysOldestFirstWithZeros() {
        val days = HistoryStats.lastDays(logOf("2026-11-18" to 400, "2026-11-12" to 900), today, 7)
        assertEquals(7, days.size)
        assertEquals(LocalDate(2026, 11, 12) to 900, days.first())
        assertEquals(LocalDate(2026, 11, 18) to 400, days.last())
        assertEquals(0, days[3].second)
    }

    @Test
    fun daysInMonthHandlesLeapYears() {
        assertEquals(29, HistoryStats.daysInMonth(2028, 2))
        assertEquals(28, HistoryStats.daysInMonth(2026, 2))
        assertEquals(31, HistoryStats.daysInMonth(2026, 12))
    }

    @Test
    fun perksUnlockCumulatively() {
        assertEquals(emptyList(), perksForStreak(0))
        assertEquals(listOf(1), perksForStreak(6))
        assertEquals(listOf(1, 3), perksForStreak(7))
        assertEquals(listOf(1, 3, 4), perksForStreak(29))
        assertEquals(listOf(1, 3, 4, 2), perksForStreak(30))
    }
}
