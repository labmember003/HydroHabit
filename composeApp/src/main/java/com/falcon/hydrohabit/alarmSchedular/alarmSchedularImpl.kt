package com.falcon.hydrohabit.alarmSchedular

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.falcon.hydrohabit.receiver.AlarmReceiver
import com.falcon.hydrohabit.model.water_reminder.WaterReminder
import java.util.Calendar

class AlarmScheduler(
    private val context: Context
) : AlarmSchedularInterface {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    /**
     * Schedule repeating water reminders between wakeUpHour and bedHour
     * at the given intervalMinutes.
     */
    fun scheduleRepeating(
        intervalMinutes: Int,
        wakeUpHour: Int = 8,
        wakeUpMinute: Int = 0,
        bedHour: Int = 22,
        bedMinute: Int = 0
    ) {
        // Cancel any existing alarms first
        cancelAll()

        val now = Calendar.getInstance()
        val isOvernightSchedule = bedHour < wakeUpHour || (bedHour == wakeUpHour && bedMinute < wakeUpMinute)
        val nowHour = now.get(Calendar.HOUR_OF_DAY)
        val nowMinute = now.get(Calendar.MINUTE)
        // Check if current time is in the still-awake-from-yesterday window (midnight → bed time)
        val isBeforeBedInOvernight = isOvernightSchedule &&
                (nowHour < bedHour || (nowHour == bedHour && nowMinute < bedMinute))

        val bedTime: Calendar
        val start: Calendar

        if (isBeforeBedInOvernight) {
            // User is still awake from yesterday (e.g., it's 12:06 AM, bed is 1 AM)
            // Schedule from now to bed time TODAY
            bedTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, bedHour)
                set(Calendar.MINUTE, bedMinute)
                set(Calendar.SECOND, 0)
            }
            start = Calendar.getInstance().apply {
                // Round up to next minute
                add(Calendar.MINUTE, 1)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } else {
            // Normal case: schedule from wake time onwards
            start = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, wakeUpHour)
                set(Calendar.MINUTE, wakeUpMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(now)) {
                    while (before(now)) {
                        add(Calendar.MINUTE, intervalMinutes)
                    }
                }
            }
            bedTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, bedHour)
                set(Calendar.MINUTE, bedMinute)
                set(Calendar.SECOND, 0)
                if (isOvernightSchedule) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }

        if (!start.before(bedTime)) {
            // Log.w(TAG, "START is after BEDTIME — no alarms will be scheduled!")
        }

        // Schedule individual alarms for today's remaining slots
        var requestCode = 1000
        var count = 0
        val maxAlarms = 50
        val current = start.clone() as Calendar
        while (current.before(bedTime) && count < maxAlarms) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("waterReminderMessage", "Time to drink water! 💧 Stay hydrated.")
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                current.timeInMillis,
                PendingIntent.getBroadcast(
                    context,
                    requestCode++,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            count++
            current.add(Calendar.MINUTE, intervalMinutes)
        }
    }

    /**
     * Cancel all scheduled water reminders
     */
    fun cancelAll() {
        for (requestCode in 1000..1050) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    override fun schedule(reminder: WaterReminder) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("waterReminderMessage", reminder.message)
        }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminder.time.hour)
            set(Calendar.MINUTE, reminder.time.minute)
            set(Calendar.SECOND, 0)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            PendingIntent.getBroadcast(
                context,
                reminder.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(reminder: WaterReminder) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                reminder.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}