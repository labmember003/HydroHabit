package com.falcon.hydrohabit.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.falcon.hydrohabit.features.notifications.NotificationChannelService

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val reminder = intent?.getStringExtra("waterReminderMessage")?:return
        val reminderNotification = NotificationChannelService(context = context)
        reminderNotification.showNotification(reminder)
    }
}