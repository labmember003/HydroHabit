package com.falcon.hydrohabit.navigation.navUtils

import hydrohabit.composeapp.generated.resources.Res
import hydrohabit.composeapp.generated.resources.home_icon
import hydrohabit.composeapp.generated.resources.ic_settings

sealed class BottomNavScreens(val route: String, val title: String, val iconRes: org.jetbrains.compose.resources.DrawableResource) {
    data object HomeScreen : BottomNavScreens(route = "Home", title = "Home", iconRes = Res.drawable.home_icon)
    data object SettingsScreen : BottomNavScreens(route = "Settings", title = "Settings", iconRes = Res.drawable.ic_settings)
}
