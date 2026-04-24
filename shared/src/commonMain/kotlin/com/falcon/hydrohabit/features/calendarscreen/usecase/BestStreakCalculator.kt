package com.falcon.hydrohabit.features.calendarscreen.usecase

/**
 * Pure function that computes the best streak value.
 * Extracted from CalendarViewModel for KMM sharing.
 *
 * @param currentBest  the previously recorded best streak
 * @param currentStreak the user's current streak count
 * @return the higher of the two values, or 0 if currentStreak is 0
 */
fun calculateBestStreak(currentBest: Int, currentStreak: Int): Int {
    if (currentStreak == 0) return 0
    return maxOf(currentBest, currentStreak)
}

