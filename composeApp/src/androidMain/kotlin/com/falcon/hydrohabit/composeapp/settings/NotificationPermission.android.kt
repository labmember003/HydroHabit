package com.falcon.hydrohabit.composeapp.settings

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
actual fun rememberNotificationPermissionState(
    onPermissionResult: (Boolean) -> Unit
): NotificationPermissionState {
    val context = LocalContext.current

    fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    var hasPermission by remember { mutableStateOf(checkPermission()) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            // SCHEDULE_EXACT_ALARM is denied by default on Android 13+. Ask for the
            // "Alarms & reminders" special access right after the user enables
            // notifications, so reminders fire at exact times instead of Doze windows.
            // Until granted, the scheduler falls back to inexact allow-while-idle alarms.
            requestExactAlarmAccessIfNeeded(context)
        }
        onPermissionResult(granted)
    }

    // Re-check permission on every resume (e.g. returning from app settings) so the
    // toggle state stays accurate. Intentionally does NOT surface any dialog here —
    // the normal system prompt happens during onboarding, and the "open settings"
    // dialog is only shown when the user actively tries to enable notifications.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = checkPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    return NotificationPermissionState(
        hasPermission = hasPermission,
        requestPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onPermissionResult(true)
            }
        },
        openAppSettings = {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
        }
    )
}

private fun requestExactAlarmAccessIfNeeded(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
    if (alarmManager.canScheduleExactAlarms()) return
    runCatching {
        context.startActivity(
            Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.fromParts("package", context.packageName, null)
            )
        )
    }
}
