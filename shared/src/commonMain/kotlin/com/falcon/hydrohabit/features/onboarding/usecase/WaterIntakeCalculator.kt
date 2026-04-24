package com.falcon.hydrohabit.features.onboarding.usecase

import kotlin.math.sqrt

/**
 * Pure business logic for calculating daily water intake.
 * Extracted from OnboardingViewModel for KMM sharing.
 *
 * Uses the Mosteller BSA formula + activity-level adjustment to compute
 * a recommended total water intake (TWI) in millilitres.
 */
object WaterIntakeCalculator {

    /**
     * @param heightCm     user height in centimetres
     * @param weightKg     user weight in kilograms
     * @param activityLevel percentage adjustment (e.g. 50 for sedentary, 35 moderate, 20 active)
     * @return recommended daily water intake in ml, clamped to [2000, 3700]
     */
    fun calculateWaterIntake(heightCm: Int, weightKg: Int, activityLevel: Int): Int {
        // BSA using the Mosteller formula
        val bsa = sqrt(heightCm.toDouble() * weightKg.toDouble() / 3600.0).toInt()

        // Base water intake: 33 ml per kg of body weight
        val bwi = weightKg * 33

        // Adjust BWI based on BSA
        val bwiAdjusted = bwi * bsa

        // Apply activity level adjustment
        val twi = bwiAdjusted + (bwiAdjusted * activityLevel / 100).toDouble()

        // Clamp to reasonable limits
        val clampedTwi = when {
            twi > 3700 -> 3700.0
            twi > 3000 && twi < 3500 -> 3500.0
            twi > 2500 && twi < 3000 -> 3000.0
            twi > 2000 && twi < 2500 -> 2500.0
            twi < 2000 -> 2000.0
            else -> twi
        }

        return clampedTwi.toInt()
    }
}

