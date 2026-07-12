package com.falcon.hydrohabit.composeapp.settings

import androidx.compose.runtime.Composable

data class PlatformActionsState(
    val sendBugReport: () -> Unit
)

@Composable
expect fun rememberPlatformActions(): PlatformActionsState
