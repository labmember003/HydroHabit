package com.falcon.hydrohabit.composeapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.falcon.hydrohabit.model.water_reminder.WaterReminder
import java.util.Calendar

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmSchedulerContract {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun scheduleRepeating(
        intervalMinutes: Int,
        wakeUpHour: Int,
        wakeUpMinute: Int,
        bedHour: Int,
        bedMinute: Int,
        soundIndex: Int
    ) {
        // soundIndex is unused on Android: the sound is resolved at display time by
        // ComposeNotificationService via per-sound notification channels.
        cancelAll()

        val now = Calendar.getInstance()
        val isOvernightSchedule = bedHour < wakeUpHour || (bedHour == wakeUpHour && bedMinute < wakeUpMinute)
        val nowHour = now.get(Calendar.HOUR_OF_DAY)
        val nowMinute = now.get(Calendar.MINUTE)
        val isBeforeBedInOvernight = isOvernightSchedule &&
                (nowHour < bedHour || (nowHour == bedHour && nowMinute < bedMinute))

        val bedTime: Calendar
        val start: Calendar

        if (isBeforeBedInOvernight) {
            bedTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, bedHour)
                set(Calendar.MINUTE, bedMinute)
                set(Calendar.SECOND, 0)
            }
            start = Calendar.getInstance().apply {
                add(Calendar.MINUTE, 1)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } else {
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
            return
        }

        var requestCode = 1000
        var count = 0
        val maxAlarms = 50
        val current = start.clone() as Calendar
        while (current.before(bedTime) && count < maxAlarms) {
            val intent = Intent(context, ComposeAlarmReceiver::class.java).apply {
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

    override fun cancelAll() {
        for (requestCode in 1000..1050) {
            val intent = Intent(context, ComposeAlarmReceiver::class.java)
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
        val intent = Intent(context, ComposeAlarmReceiver::class.java).apply {
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
                Intent(context, ComposeAlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
