package com.falcon.hydrohabit.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import com.falcon.hydrohabit.BuildConfig
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

actual fun logDebug(tag: String, message: String) {
    Log.d(tag, message)
}

actual fun logError(tag: String, message: String) {
    Log.e(tag, message)
}

actual fun currentDateString(): String = LocalDate.now().toString()

actual fun currentHourOfDay(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

actual fun currentMinute(): Int = LocalTime.now().minute

actual fun currentDayOfMonth(): Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

actual fun platformName(): String = "Android"

actual fun isDebugBuild(): Boolean = BuildConfig.DEBUG

// These need context, handled via PlatformContext
private var appContextRef: Context? = null
private var currentViewRef: View? = null

fun initPlatformContext(context: Context) {
    appContextRef = context.applicationContext
}

fun setCurrentView(view: View) {
    currentViewRef = view
}

actual fun performHapticFeedback() {
    currentViewRef?.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
}

actual fun openUrl(url: String) {
    val ctx = appContextRef ?: return
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    ctx.startActivity(intent)
}

actual fun sendBugReportEmail() {
    val ctx = appContextRef ?: return
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("falcontechlab@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "HydroHabit - Bug Report")
        putExtra(Intent.EXTRA_TEXT, "Please describe the bug you encountered:\n\n")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        ctx.startActivity(Intent.createChooser(intent, "Send Bug Report").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    } catch (_: Exception) {}
}

actual fun openAppSettings() {
    val ctx = appContextRef ?: return
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", ctx.packageName, null)).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    ctx.startActivity(intent)
}
