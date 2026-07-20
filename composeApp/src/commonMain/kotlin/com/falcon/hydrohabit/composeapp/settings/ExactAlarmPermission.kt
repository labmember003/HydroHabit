package com.falcon.hydrohabit.composeapp.settings

import androidx.compose.runtime.Composable

data class ExactAlarmState(
    // True only where exact-alarm access is a real, user-grantable permission that is
    // currently NOT granted (Android 12+). Always false on iOS and on Android versions
    // where exact alarms are granted at install — so the warning is hidden there.
    val showPrecisionWarning: Boolean,
    val openExactAlarmSettings: () -> Unit,
)

@Composable
expect fun rememberExactAlarmState(): ExactAlarmState
