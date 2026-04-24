package com.falcon.hydrohabit.features.homescreen.usecase

/**
 * Pure function that calculates water intake percentage.
 * Extracted from HomeViewModel for KMM sharing.
 *
 * @param usedWater  the amount of water consumed (ml)
 * @param totalWater the daily target (ml)
 * @return percentage (0–100), clamped; returns 0 if inputs are invalid
 */
fun calculateWaterPercent(usedWater: Int, totalWater: Int): Int {
    if (totalWater <= 0) return 0
    if (usedWater <= 0) return 0
    val percent = usedWater * 100 / totalWater
    return if (percent > 100) 100 else percent
}