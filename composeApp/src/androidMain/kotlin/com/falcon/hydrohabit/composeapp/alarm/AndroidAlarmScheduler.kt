package com.falcon.hydrohabit.composeapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.falcon.hydrohabit.model.water_reminder.WaterReminder
import java.util.Calendar

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmSchedulerContract {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    // SCHEDULE_EXACT_ALARM is denied by default on Android 13+; calling setExact* without
    // the grant throws SecurityException. Fall back to setAndAllowWhileIdle, which still
    // fires during Doze (just up to ~15 min late).
    private fun canUseExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    private fun setExactCompat(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        if (canUseExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
            )
        }
    }

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

        // One-shot alarms only cover today, so a daily refresh alarm re-runs this
        // scheduling shortly before tomorrow's wake-up — otherwise reminders stop
        // on any day the app is not opened.
        armDailyRefresh(wakeUpHour, wakeUpMinute)

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
            setExactCompat(
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

    // Fires ComposeRescheduleReceiver 3 min before the next wake-up so the whole day
    // (including the wake-up slot itself) gets scheduled, then re-arms itself.
    private fun armDailyRefresh(wakeUpHour: Int, wakeUpMinute: Int) {
        val trigger = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, wakeUpHour)
            set(Calendar.MINUTE, wakeUpMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, -3)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        setExactCompat(trigger.timeInMillis, dailyRefreshPendingIntent())
    }

    private fun dailyRefreshPendingIntent(): PendingIntent {
        val intent = Intent(context, ComposeRescheduleReceiver::class.java).apply {
            action = ComposeRescheduleReceiver.ACTION_REARM
        }
        return PendingIntent.getBroadcast(
            context,
            DAILY_REFRESH_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
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
        alarmManager.cancel(dailyRefreshPendingIntent())
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

    companion object {
        private const val DAILY_REFRESH_REQUEST_CODE = 999
    }
}
