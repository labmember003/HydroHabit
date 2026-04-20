package com.falcon.hydrohabit.features.onboarding.utils

data class BodyMeasurementData(
    val onWeightChange: String,
    val onHeightChange: String,
    val onWeightCheck: Boolean,
    val onHeightCheck: Boolean,
    val onWeightError: String,
    val onHeightError: String
)
