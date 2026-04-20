package com.falcon.hydrohabit.features.profilescreen.utils

data class ProfileData(
    val onNotificationChange: Boolean,
    val selectedIntervalIndex: Int = 1,
    val wakeUpHour: Int = 8,
    val wakeUpMinute: Int = 0,
    val bedHour: Int = 22,
    val bedMinute: Int = 0,
    val selectedSoundIndex: Int = 0,
    val customSoundUri: String? = null,
)
