package com.falcon.hydrohabit.features.onboarding.usecase

import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Daily drinking-water target, anchored to the US Institute of Medicine (2004)
 * Dietary Reference Intakes for water.
 *
 * The IOM publishes one Adequate Intake per gender, for a reference adult body:
 * men 3.7 L/day total water of which 3.0 L comes from beverages, women 2.7 L and
 * 2.2 L. Those figures are converted into a rate per m² of body surface area
 * using the DRI reference heights and weights, which is what lets the target
 * scale with the user's own body instead of being a flat number.
 *
 * Only the beverage portion is used, because the app tracks water the user
 * drinks — the remaining ~19% of total water comes from food and is not logged.
 *
 * Training volume is added on top of the baseline, following ACSM's 0.4-0.8 L/h
 * guidance for fluid replacement during exercise.
 *
 * This is an estimate for healthy adults in a temperate climate. It is not
 * medical advice and does not account for pregnancy, illness or heat exposure.
 */
object WaterIntakeCalculator {

    const val ACTIVITY_HIGH = 0
    const val ACTIVITY_MODERATE = 1
    const val ACTIVITY_MINIMAL = 2

    // IOM 2004 Adequate Intake, beverage portion only, for the DRI reference adult (19-30 yr).
    private const val REFERENCE_MALE_HEIGHT_CM = 176.0
    private const val REFERENCE_MALE_WEIGHT_KG = 76.0
    private const val REFERENCE_MALE_BEVERAGE_ML = 3000.0

    private const val REFERENCE_FEMALE_HEIGHT_CM = 163.0
    private const val REFERENCE_FEMALE_WEIGHT_KG = 61.0
    private const val REFERENCE_FEMALE_BEVERAGE_ML = 2200.0

    // ACSM advises 0.4-0.8 L/h during exercise; 500 ml covers a typical hour-long session
    // at the conservative end. Sessions are spread across the week into a flat daily bonus.
    private const val ML_PER_TRAINING_SESSION = 500.0
    private const val DAYS_PER_WEEK = 7.0

    private const val MIN_INTAKE_ML = 1500
    private const val MAX_INTAKE_ML = 4500
    private const val ROUNDING_ML = 50

    private val maleMlPerSquareMetre =
        REFERENCE_MALE_BEVERAGE_ML / bodySurfaceArea(REFERENCE_MALE_HEIGHT_CM, REFERENCE_MALE_WEIGHT_KG)

    private val femaleMlPerSquareMetre =
        REFERENCE_FEMALE_BEVERAGE_ML / bodySurfaceArea(REFERENCE_FEMALE_HEIGHT_CM, REFERENCE_FEMALE_WEIGHT_KG)

    private val unspecifiedMlPerSquareMetre =
        (maleMlPerSquareMetre + femaleMlPerSquareMetre) / 2.0

    /**
     * @param heightCm      user height in centimetres
     * @param weightKg      user weight in kilograms
     * @param activityLevel one of [ACTIVITY_HIGH], [ACTIVITY_MODERATE], [ACTIVITY_MINIMAL]
     * @return daily drinking-water target in ml, rounded to 50 ml, clamped to [1500, 4500]
     */
    fun calculateWaterIntake(heightCm: Int, weightKg: Int, activityLevel: Int, gender: Gender): Int {
        if (heightCm <= 0 || weightKg <= 0) return MIN_INTAKE_ML

        val mlPerSquareMetre = when (gender) {
            Gender.MALE -> maleMlPerSquareMetre
            Gender.FEMALE -> femaleMlPerSquareMetre
            Gender.UNSPECIFIED -> unspecifiedMlPerSquareMetre
        }

        val sessionsPerWeek = when (activityLevel) {
            ACTIVITY_HIGH -> 5.5
            ACTIVITY_MODERATE -> 2.5
            else -> 0.0
        }

        val baselineMl = bodySurfaceArea(heightCm.toDouble(), weightKg.toDouble()) * mlPerSquareMetre
        val trainingMl = sessionsPerWeek / DAYS_PER_WEEK * ML_PER_TRAINING_SESSION
        val rounded = ((baselineMl + trainingMl) / ROUNDING_ML).roundToInt() * ROUNDING_ML

        return rounded.coerceIn(MIN_INTAKE_ML, MAX_INTAKE_ML)
    }

    /** Mosteller body surface area in m². */
    private fun bodySurfaceArea(heightCm: Double, weightKg: Double): Double =
        sqrt(heightCm * weightKg / 3600.0)
}
