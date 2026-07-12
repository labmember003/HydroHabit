package com.falcon.hydrohabit.composeapp.settings

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun rememberPlatformActions(): PlatformActionsState {
    return PlatformActionsState(
        sendBugReport = {
            val urlString = "mailto:falcontechlab@gmail.com?subject=HydroHabit%20-%20Bug%20Report&body=Please%20describe%20the%20bug%20you%20encountered%3A%0A%0A"
            NSURL.URLWithString(urlString)?.let { url ->
                UIApplication.sharedApplication.openURL(url)
            }
        }
    )
}
