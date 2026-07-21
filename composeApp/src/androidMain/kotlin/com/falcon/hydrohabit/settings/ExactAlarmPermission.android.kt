package com.falcon.hydrohabit.settings

import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
actual fun rememberExactAlarmState(): ExactAlarmState {
    val context = LocalContext.current

    // Exact-alarm access is a real, user-grantable permission only on Android 12+
    // (API 31). Below that it is granted at install, so there is nothing to warn about.
    fun canScheduleExact(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return true
        return alarmManager.canScheduleExactAlarms()
    }

    var granted by remember { mutableStateOf(canScheduleExact()) }

    // Re-check when returning from the system "Alarms & reminders" settings page so the
    // warning disappears immediately once the user grants access.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                granted = canScheduleExact()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    return ExactAlarmState(
        showPrecisionWarning = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !granted,
        openExactAlarmSettings = {
            runCatching {
                context.startActivity(
                    Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.fromParts("package", context.packageName, null)
                    )
                )
            }
        }
    )
}
