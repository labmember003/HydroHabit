package com.falcon.hydrohabit.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class SnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Dismiss the current notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1001)

        // Schedule a new alarm 5 minutes from now
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("waterReminderMessage", "Time to drink water! 💧 Stay hydrated.")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2000, // Unique request code for snooze
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = System.currentTimeMillis() + 5 * 60 * 1000L
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }
}

