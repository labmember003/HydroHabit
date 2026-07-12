package com.falcon.hydrohabit.composeapp.alarm

import com.falcon.hydrohabit.model.water_reminder.WaterReminder
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class IosAlarmScheduler : AlarmSchedulerContract {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    private val idPrefix = "water_reminder_"

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

        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                println("iOS: Failed to schedule notification: ${error.localizedDescription}")
            }
        }
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
        bedMinute: Int
    ) {
        cancelAll()

        val calendar = NSCalendar.currentCalendar
        val now = NSDate()
        val nowComponents = calendar.components(
            NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitDay,
            fromDate = now
        )
        val nowHour = nowComponents.hour.toInt()
        val nowMinute = nowComponents.minute.toInt()

        val isOvernightSchedule = bedHour < wakeUpHour ||
                (bedHour == wakeUpHour && bedMinute < wakeUpMinute)

        val startTotalMinutes: Int
        val endTotalMinutes: Int

        if (isOvernightSchedule) {
            val isBeforeBed = nowHour < bedHour || (nowHour == bedHour && nowMinute < bedMinute)
            if (isBeforeBed) {
                val nowTotal = nowHour * 60 + nowMinute
                startTotalMinutes = nowTotal + 1
                endTotalMinutes = bedHour * 60 + bedMinute
            } else {
                val wakeTotal = wakeUpHour * 60 + wakeUpMinute
                var current = wakeTotal
                val nowTotal = nowHour * 60 + nowMinute
                while (current <= nowTotal) {
                    current += intervalMinutes
                }
                startTotalMinutes = current
                endTotalMinutes = (bedHour + 24) * 60 + bedMinute
            }
        } else {
            val wakeTotal = wakeUpHour * 60 + wakeUpMinute
            val nowTotal = nowHour * 60 + nowMinute
            var current = wakeTotal
            if (nowTotal > wakeTotal) {
                while (current <= nowTotal) {
                    current += intervalMinutes
                }
            }
            startTotalMinutes = current
            endTotalMinutes = bedHour * 60 + bedMinute
        }

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
                setSound(UNNotificationSound.defaultSound)
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

            notificationCenter.addNotificationRequest(request) { error ->
                if (error != null) {
                    println("iOS: Failed to schedule notification: ${error.localizedDescription}")
                }
            }

            count++
            currentMinutes += intervalMinutes
        }
    }

    override fun cancelAll() {
        notificationCenter.removeAllPendingNotificationRequests()
    }
}
