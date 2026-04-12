package com.falcon.hydrohabit.navigation.navUtils

import com.falcon.hydrohabit.R

sealed class OnboardingNavScreens(val route:String, val icon:Int, ) {
    data object BodyMeasurementScreen: OnboardingNavScreens(route = "BodyMeasurementScreen", icon = R.drawable.home_icon)
    data object ActivityIntakeScreen: OnboardingNavScreens(route = "ActivityIntakeScreen", icon = R.drawable.calendar_icon)
    data object WaterIntakeResultScreen: OnboardingNavScreens(route = "WaterIntakeResultScreen", icon = R.drawable.calendar_icon)
    data object LoadingScreen: OnboardingNavScreens(route = "LoadingScreen", icon = R.drawable.calendar_icon)
    data object PremiumScreen: OnboardingNavScreens(route = "PremiumScreen", icon = R.drawable.calendar_icon)
    data object ReminderPermissionScreen: OnboardingNavScreens(route = "ReminderPermissionScreen", icon = R.drawable.calendar_icon)
    data object NotificationPermissionScreen: OnboardingNavScreens(route = "NotificationPermissionScreen", icon = R.drawable.calendar_icon)
}