package com.falcon.hydrohabit.platform

import platform.UserNotifications.*
import platform.Foundation.*

actual class NotificationScheduler {

    actual fun scheduleRepeating(
        intervalMinutes: Int,
        wakeUpHour: Int,
        wakeUpMinute: Int,
        bedHour: Int,
        bedMinute: Int
    ) {
        cancelAll()

        // Request notification permission
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, error ->
            if (granted) {
                // Schedule notifications between wake and bed time
                val content = UNMutableNotificationContent().apply {
                    setTitle("Water Reminder 💧")
                    setBody("Time to drink water! Stay hydrated.")
                    setSound(UNNotificationSound.defaultSound())
                }

                // Create a repeating trigger based on interval
                val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                    (intervalMinutes * 60).toDouble(),
                    repeats = true
                )

                val request = UNNotificationRequest.requestWithIdentifier(
                    "water_reminder",
                    content = content,
                    trigger = trigger
                )

                center.addNotificationRequest(request) { error ->
                    error?.let { println("Failed to schedule notification: ${it.localizedDescription}") }
                }
            }
        }
    }

    actual fun cancelAll() {
        UNUserNotificationCenter.currentNotificationCenter().removeAllPendingNotificationRequests()
    }

    actual fun showNotification(title: String, message: String) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setSound(UNNotificationSound.defaultSound())
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(
            "immediate_${NSDate().timeIntervalSince1970}",
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request, null)
    }
}

