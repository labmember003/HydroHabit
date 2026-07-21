package com.falcon.hydrohabit.alarm

import com.falcon.hydrohabit.model.water_reminder.WaterReminder
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionList
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

class IosAlarmScheduler : AlarmSchedulerContract {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    private val idPrefix = "water_reminder_"

    // Held as a strong reference for the app's lifetime (this scheduler is a Koin
    // singleton). Without a delegate, iOS silently drops any reminder that fires while
    // the app is in the foreground, so it would never appear while Droply is open.
    private val foregroundDelegate = ForegroundNotificationDelegate()

    init {
        notificationCenter.setDelegate(foregroundDelegate)
    }

    // Maps the selected sound index to a bundled WAV, matching the Android raw sounds.
    // Index 0-4 → water_drop_1..5.wav; anything else (System Default / Custom) → system default.
    private fun soundForIndex(soundIndex: Int): UNNotificationSound {
        val fileName = when (soundIndex) {
            0 -> "water_drop_1.wav"
            1 -> "water_drop_2.wav"
            2 -> "water_drop_3.wav"
            3 -> "water_drop_4.wav"
            4 -> "water_drop_5.wav"
            else -> null
        }
        return if (fileName != null) {
            UNNotificationSound.soundNamed(fileName)
        } else {
            UNNotificationSound.defaultSound
        }
    }

    override fun schedule(reminder: WaterReminder) {
        val content = UNMutableNotificationContent().apply {
            setTitle("Water Reminder 💧")
            setBody(reminder.message)
            setSound(UNNotificationSound.defaultSound)
        }

        val dateComponents = NSDateComponents().apply {
            hour = reminder.time.hour.toLong()
            minute = reminder.time.minute.toLong()
            second = 0
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = dateComponents,
            repeats = true
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "$idPrefix${reminder.hashCode()}",
            content = content,
            trigger = trigger
        )

        notificationCenter.addNotificationRequest(request) { }
    }

    override fun cancel(reminder: WaterReminder) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf("$idPrefix${reminder.hashCode()}")
        )
    }

    override fun scheduleRepeating(
        intervalMinutes: Int,
        wakeUpHour: Int,
        wakeUpMinute: Int,
        bedHour: Int,
        bedMinute: Int,
        soundIndex: Int
    ) {
        cancelAll()
        val notificationSound = soundForIndex(soundIndex)

        // Each slot below is a repeats=true daily calendar trigger, so the ENTIRE
        // wake→bed window must be scheduled every time — independent of the current
        // time. iOS fires each slot at its next matching instant (later today if still
        // ahead, otherwise tomorrow) and then every day after, so it never fires a
        // past-today slot immediately. Computing the start from "now" (the old code)
        // permanently dropped earlier slots from the daily schedule, and wiped the
        // schedule entirely when the app was opened after bedtime.
        val startTotalMinutes = wakeUpHour * 60 + wakeUpMinute
        val bedTotalMinutes = bedHour * 60 + bedMinute
        val isOvernightSchedule = bedHour < wakeUpHour ||
                (bedHour == wakeUpHour && bedMinute < wakeUpMinute)
        val endTotalMinutes = if (isOvernightSchedule) bedTotalMinutes + 24 * 60 else bedTotalMinutes

        if (startTotalMinutes >= endTotalMinutes) return

        var count = 0
        val maxNotifications = 50
        var currentMinutes = startTotalMinutes

        while (currentMinutes < endTotalMinutes && count < maxNotifications) {
            val hour = (currentMinutes / 60) % 24
            val minute = currentMinutes % 60

            val content = UNMutableNotificationContent().apply {
                setTitle("Water Reminder 💧")
                setBody("Time to drink water! Stay hydrated.")
                setSound(notificationSound)
            }

            val dateComponents = NSDateComponents().apply {
                this.hour = hour.toLong()
                this.minute = minute.toLong()
                this.second = 0
            }

            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = dateComponents,
                repeats = true
            )

            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = "${idPrefix}repeating_$count",
                content = content,
                trigger = trigger
            )

            notificationCenter.addNotificationRequest(request) { }

            count++
            currentMinutes += intervalMinutes
        }
    }

    override fun cancelAll() {
        notificationCenter.removeAllPendingNotificationRequests()
    }
}

// Presents reminders even when the app is in the foreground; iOS otherwise silently
// drops any notification that fires while the app is open.
private class ForegroundNotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
    ) {
        withCompletionHandler(
            UNNotificationPresentationOptionBanner or
                UNNotificationPresentationOptionList or
                UNNotificationPresentationOptionSound
        )
    }

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit
    ) {
        withCompletionHandler()
    }
}
