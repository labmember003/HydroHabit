package com.falcon.hydrohabit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.falcon.hydrohabit.di.DIModule
import com.falcon.hydrohabit.features.notifications.NotificationChannelService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseAppHydroHabit:Application(){
    override fun onCreate() {

        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseAppHydroHabit)
            modules(DIModule)
        }
        createNotificationChannel()
    }


    // Delete legacy notification channel and let NotificationChannelService handle channels per sound
    private fun createNotificationChannel(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Remove old channel if it exists
        notificationManager.deleteNotificationChannel("Water_reminder_notification_ID")
    }
}