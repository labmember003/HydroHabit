package com.falcon.hydrohabit.platform

/**
 * Platform-specific functionality exposed via expect/actual.
 */

/** Simple logging abstraction */
expect fun logDebug(tag: String, message: String)
expect fun logError(tag: String, message: String)

/** Get current date as ISO string (yyyy-MM-dd) */
expect fun currentDateString(): String

/** Get current hour of day (0-23) */
expect fun currentHourOfDay(): Int

/** Get current minute */
expect fun currentMinute(): Int

/** Get current day of month */
expect fun currentDayOfMonth(): Int

/** Platform name */
expect fun platformName(): String

/** Whether the app is a debug build */
expect fun isDebugBuild(): Boolean

/** Perform haptic feedback (clock tick) */
expect fun performHapticFeedback()

/** Open URL (for email, settings, etc.) */
expect fun openUrl(url: String)

/** Send bug report email */
expect fun sendBugReportEmail()

/** Open app settings */
expect fun openAppSettings()
