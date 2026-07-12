package com.falcon.hydrohabit.composeapp.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

@Composable
actual fun rememberNotificationPermissionState(
    onPermissionResult: (Boolean) -> Unit
): NotificationPermissionState {
    val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    var hasPermission by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            val granted = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
            hasPermission = granted
        }
        onDispose { }
    }

    return NotificationPermissionState(
        hasPermission = hasPermission,
        requestPermission = {
            notificationCenter.requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
            ) { granted, _ ->
                hasPermission = granted
                onPermissionResult(granted)
            }
        },
        openAppSettings = {
            val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
            if (url != null) {
                UIApplication.sharedApplication.openURL(
                    url,
                    options = mapOf<Any?, Any?>(),
                    completionHandler = null
                )
            }
        }
    )
}
