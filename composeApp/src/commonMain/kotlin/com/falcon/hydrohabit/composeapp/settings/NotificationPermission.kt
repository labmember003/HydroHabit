package com.falcon.hydrohabit.composeapp.settings

import androidx.compose.runtime.Composable

data class NotificationPermissionState(
    val hasPermission: Boolean,
    val requestPermission: () -> Unit,
    val openAppSettings: () -> Unit
)

@Composable
expect fun rememberNotificationPermissionState(
    onPermissionResult: (Boolean) -> Unit
): NotificationPermissionState
