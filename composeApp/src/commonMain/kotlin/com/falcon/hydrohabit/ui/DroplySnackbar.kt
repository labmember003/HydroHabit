package com.falcon.hydrohabit.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** iOS-style dark toast with the app's blue action text. */
@Composable
fun DroplySnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        Snackbar(
            snackbarData = data,
            shape = RoundedCornerShape(14.dp),
            containerColor = Color(0xFF1C1C1E),
            contentColor = Color.White,
            actionColor = Color(0xFF0A84FF),
        )
    }
}

/** Shows one message at a time, replacing whatever is on screen. */
fun SnackbarHostState.show(
    scope: CoroutineScope,
    message: String,
    actionLabel: String?,
    onAction: (() -> Unit)?,
) {
    currentSnackbarData?.dismiss()
    scope.launch {
        // Messages with an action (Undo) stay up longer so there is time to reach them.
        val duration = if (actionLabel != null) SnackbarDuration.Long else SnackbarDuration.Short
        val result = showSnackbar(message = message, actionLabel = actionLabel, duration = duration)
        if (result == SnackbarResult.ActionPerformed) onAction?.invoke()
    }
}
