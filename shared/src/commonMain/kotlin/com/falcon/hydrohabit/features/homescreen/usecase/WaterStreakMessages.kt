package com.falcon.hydrohabit.features.homescreen.usecase

/**
 * Pure function that returns an encouragement message based on water intake progress.
 * Extracted from HomeViewModel for KMM sharing.
 *
 * @param waterPercent the current water intake as a percentage (0–100)
 * @return a motivational message string
 */
fun getStreakMessage(waterPercent: Int): String {
    return when (waterPercent) {
        in 0..30 -> "Keep Going, You are doing Great"
        in 30..50 -> "You are half way through, keep it up"
        in 80..95 -> "You are almost there, keep it going "
        in 95..100 -> "Amazing"
        else -> ""
    }
}