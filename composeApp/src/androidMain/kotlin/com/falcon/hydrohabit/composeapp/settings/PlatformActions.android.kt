package com.falcon.hydrohabit.composeapp.settings

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
actual fun rememberPlatformActions(): PlatformActionsState {
    val context = LocalContext.current
    return PlatformActionsState(
        sendBugReport = {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf("falcontechlab@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "HydroHabit - Bug Report")
                putExtra(Intent.EXTRA_TEXT, "Please describe the bug you encountered:\n\n")
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                context.startActivity(Intent.createChooser(intent, "Send Bug Report"))
            }
        }
    )
}
