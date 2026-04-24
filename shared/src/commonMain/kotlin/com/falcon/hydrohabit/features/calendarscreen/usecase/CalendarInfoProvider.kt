package com.falcon.hydrohabit.features.calendarscreen.usecase

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Pure multiplatform provider for calendar info.
 * Replaces java.util.Calendar usage in CalendarViewModel.
 */
object CalendarInfoProvider {

    private val monthNames = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    /**
     * Returns the display name of the current month (e.g. "April").
     */
    fun getCurrentMonthName(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return monthNames[now.monthNumber - 1]
    }

    /**
     * Returns the number of days in the current month.
     */
    fun getDaysInCurrentMonth(): Int {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val year = now.year
        val month = now.month
        // Get the last day of the month by creating a date on day 1 of the next month and subtracting 1
        return when (month) {
            Month.JANUARY -> 31
            Month.FEBRUARY -> if (isLeapYear(year)) 29 else 28
            Month.MARCH -> 31
            Month.APRIL -> 30
            Month.MAY -> 31
            Month.JUNE -> 30
            Month.JULY -> 31
            Month.AUGUST -> 31
            Month.SEPTEMBER -> 30
            Month.OCTOBER -> 31
            Month.NOVEMBER -> 30
            Month.DECEMBER -> 31
            else -> 30
        }
    }

    /**
     * Returns the current day of month (1-based).
     */
    fun getCurrentDayOfMonth(): Int {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfMonth
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}

