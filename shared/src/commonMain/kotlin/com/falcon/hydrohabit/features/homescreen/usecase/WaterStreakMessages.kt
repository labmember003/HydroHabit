package com.falcon.hydrohabit.features.homescreen.usecase

/**
 * Pure function that returns an encouragement message based on water intake progress.
 * Extracted from HomeViewModel for KMM sharing.
 *
 * @param waterPercent the current water intake as a percentage (0–100)
 * @return a motivational message string
 */
fun getStreakMessage(waterPercent: Int): String {
    return when {
        waterPercent <= 0 -> "Log your first glass to get started"
        waterPercent < 50 -> "Good start, keep sipping"
        waterPercent < 80 -> "Halfway there, keep it up"
        waterPercent < 100 -> "Almost there, keep going"
        else -> "Goal reached. Amazing!"
    }
}