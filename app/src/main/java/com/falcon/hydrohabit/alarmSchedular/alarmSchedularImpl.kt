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
        bedHour: Int = 22
    ) {
        // Cancel any existing alarms first
        cancelAll()

        val now = Calendar.getInstance()
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, wakeUpHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If wake time already passed today, start from next interval
            if (before(now)) {
                // Find the next alarm time after now
                while (before(now)) {
                    add(Calendar.MINUTE, intervalMinutes)
                }
            }
        }

        val bedTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, bedHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // Schedule individual alarms for today's remaining slots
        var requestCode = 1000
        val current = start.clone() as Calendar
        while (current.before(bedTime)) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("waterReminderMessage", "Time to drink water! 💧 Stay hydrated.")
            }
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                current.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                PendingIntent.getBroadcast(
                    context,
                    requestCode++,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
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