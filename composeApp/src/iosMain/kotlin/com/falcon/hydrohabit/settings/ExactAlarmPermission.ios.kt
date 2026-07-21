package com.falcon.hydrohabit.settings

import androidx.compose.runtime.Composable

// iOS has no "exact alarm" special access — local notifications always fire at their
// scheduled time — so the precision warning is never shown here.
@Composable
actual fun rememberExactAlarmState(): ExactAlarmState = ExactAlarmState(
    showPrecisionWarning = false,
    openExactAlarmSettings = {}
)
