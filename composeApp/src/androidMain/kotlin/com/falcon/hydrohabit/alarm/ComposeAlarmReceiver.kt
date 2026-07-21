package com.falcon.hydrohabit.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ComposeAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val reminder = intent?.getStringExtra("waterReminderMessage") ?: return
        val notificationService = ComposeNotificationService(context)
        notificationService.showNotification(reminder)
    }
}
