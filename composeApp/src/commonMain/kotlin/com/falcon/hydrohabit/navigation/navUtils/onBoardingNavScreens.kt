package com.falcon.hydrohabit.navigation.navUtils

sealed class OnboardingNavScreens(val route: String) {
    data object BodyMeasurementScreen : OnboardingNavScreens(route = "BodyMeasurementScreen")
    data object ActivityIntakeScreen : OnboardingNavScreens(route = "ActivityIntakeScreen")
    data object WaterIntakeResultScreen : OnboardingNavScreens(route = "WaterIntakeResultScreen")
    data object SleepScheduleScreen : OnboardingNavScreens(route = "SleepScheduleScreen")
    data object LoadingScreen : OnboardingNavScreens(route = "LoadingScreen")
    data object PremiumScreen : OnboardingNavScreens(route = "PremiumScreen")
    data object ReminderPermissionScreen : OnboardingNavScreens(route = "ReminderPermissionScreen")
    data object NotificationPermissionScreen : OnboardingNavScreens(route = "NotificationPermissionScreen")
}

