package com.falcon.hydrohabit.platform

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.falcon.hydrohabit.R
import java.util.Calendar

actual class NotificationScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    actual fun scheduleRepeating(
        intervalMinutes: Int,
        wakeUpHour: Int,
        wakeUpMinute: Int,
        bedHour: Int,
        bedMinute: Int
    ) {
        cancelAll()
        val now = Calendar.getInstance()
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, wakeUpHour)
            set(Calendar.MINUTE, wakeUpMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                while (before(now)) add(Calendar.MINUTE, intervalMinutes)
            }
        }
        val bedTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, bedHour)
            set(Calendar.MINUTE, bedMinute)
            set(Calendar.SECOND, 0)
            if (bedHour < wakeUpHour) add(Calendar.DAY_OF_YEAR, 1)
        }

        var requestCode = 1000
        var count = 0
        val current = start.clone() as Calendar
        while (current.before(bedTime) && count < 50) {
            val intent = Intent(context, com.falcon.hydrohabit.receiver.AlarmReceiver::class.java).apply {
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

    actual fun cancelAll() {
        for (requestCode in 1000..1050) {
            val intent = Intent(context, com.falcon.hydrohabit.receiver.AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(pendingIntent)
        }
    }

    actual fun showNotification(title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "water_reminder_channel"
        if (notificationManager.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(channelId, "Water Reminders", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
        notificationManager.notify(1001, builder.build())
    }
}

