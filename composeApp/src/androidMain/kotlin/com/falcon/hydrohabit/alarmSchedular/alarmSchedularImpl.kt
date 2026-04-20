package com.falcon.hydrohabit.alarmSchedular

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.falcon.hydrohabit.receiver.AlarmReceiver
import java.util.Calendar

class AlarmScheduler(
    private val context: Context
) {
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
        val start: Calendar
        val bedTime: Calendar

        start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, wakeUpHour)
            set(Calendar.MINUTE, wakeUpMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                while (before(now)) add(Calendar.MINUTE, intervalMinutes)
            }
        }
        bedTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, bedHour)
            set(Calendar.MINUTE, bedMinute)
            set(Calendar.SECOND, 0)
            if (isOvernightSchedule) add(Calendar.DAY_OF_YEAR, 1)
        }

        // Schedule individual alarms for today's remaining slots
        var requestCode = 1000
        var count = 0
        val current = start.clone() as Calendar
        while (current.before(bedTime) && count < 50) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("waterReminderMessage", "Time to drink water! 💧 Stay hydrated.")
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                current.timeInMillis,
                PendingIntent.getBroadcast(context, requestCode++, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
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
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(pendingIntent)
        }
    }
}