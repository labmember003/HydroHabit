package com.falcon.hydrohabit.features.onboarding.source

import kotlinx.serialization.Serializable

/**
 * KMM-compatible app preferences, replacing Android SharedPreferences.
 *
 * All keys previously stored in SharedPreferences("prefs") are now here:
 * - onBoardingCompleted → onboardingCompleted
 * - notifications_enabled → notificationsEnabled
 * - wake_up_hour / wake_up_minute → wakeUpHour / wakeUpMinute
 * - bed_hour / bed_minute → bedHour / bedMinute
 * - notification_interval_index → notificationIntervalIndex
 * - notification_sound_index → notificationSoundIndex
 * - custom_sound_uri → customSoundUri
 */
@Serializable
data class AppPreferences(
    val onboardingCompleted: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val wakeUpHour: Int = 8,
    val wakeUpMinute: Int = 0,
    val bedHour: Int = 22,
    val bedMinute: Int = 0,
    val notificationIntervalIndex: Int = 1,
    val notificationSoundIndex: Int = 0,
    val customSoundUri: String? = null,
    val weightUnit: String = "KG",
)

