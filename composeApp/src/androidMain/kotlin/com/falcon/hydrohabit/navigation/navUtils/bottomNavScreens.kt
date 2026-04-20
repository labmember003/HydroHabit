package com.falcon.hydrohabit.navigation.navUtils

import com.falcon.hydrohabit.R

sealed class BottomNavScreens(val route:String, val icon:Int, ) {
    data object HomeScreen: BottomNavScreens(route = "Home", icon = R.drawable.home_icon)
//    data object CalendarScreen: BottomNavScreens(route = "Track", icon = R.drawable.calendar_icon)
    data object SettingsScreen: BottomNavScreens(route = "Settings", icon = R.drawable.ic_settings)

}