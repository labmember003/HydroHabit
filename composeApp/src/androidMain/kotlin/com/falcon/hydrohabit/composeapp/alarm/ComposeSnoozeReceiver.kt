package com.falcon.hydrohabit.composeapp.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ComposeSnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1001)

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val alarmIntent = Intent(context, ComposeAlarmReceiver::class.java).apply {
            putExtra("waterReminderMessage", "Time to drink water! 💧 Stay hydrated.")
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 5 * 60 * 1000L,
            PendingIntent.getBroadcast(
                context,
                2000,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
