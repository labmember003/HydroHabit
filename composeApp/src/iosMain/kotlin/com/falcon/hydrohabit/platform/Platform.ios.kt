package com.falcon.hydrohabit.platform

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun logDebug(tag: String, message: String) {
    println("DEBUG [$tag]: $message")
}

actual fun logError(tag: String, message: String) {
    println("ERROR [$tag]: $message")
}

actual fun currentDateString(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')}"
}

actual fun currentHourOfDay(): Int {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
}

actual fun currentMinute(): Int {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).minute
}

actual fun currentDayOfMonth(): Int {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfMonth
}

actual fun platformName(): String = "iOS"

actual fun isDebugBuild(): Boolean = true // Always true for now on iOS dev builds

actual fun performHapticFeedback() {
    // iOS haptic feedback via UIImpactFeedbackGenerator
    // Requires UIKit interop - simplified for now
}

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
}

actual fun sendBugReportEmail() {
    openUrl("mailto:falcontechlab@gmail.com?subject=HydroHabit%20-%20Bug%20Report&body=Please%20describe%20the%20bug%20you%20encountered%3A%0A%0A")
}

actual fun openAppSettings() {
    val url = NSURL.URLWithString("app-settings:") ?: return
    UIApplication.sharedApplication.openURL(url)
}
