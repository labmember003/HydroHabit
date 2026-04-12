package com.falcon.hydrohabit.features.profilescreen.utils

data class profileData(
    val onNotificationChange: Boolean,
    val selectedIntervalIndex: Int = 1, // default: 1 hour (health-recommended)
    val wakeUpHour: Int = 8,  // default: 8 AM
    val bedHour: Int = 22,    // default: 10 PM
)
