package com.falcon.hydrohabit.features.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.falcon.hydrohabit.MainActivity
import com.falcon.hydrohabit.R
import androidx.core.net.toUri

class NotificationChannelService(
    private val context: Context
) : NotificationChannelInterface {

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        val notificationIDWaterReminder = "Water_reminder_notification_ID"
    }

    override fun showNotification(reminder: String) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1234,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val soundIndex = prefs.getInt("notification_sound_index", 0)

        val builder = NotificationCompat.Builder(context, notificationIDWaterReminder)
            .setSmallIcon(R.drawable.hydrohabit_logo)
            .setContentTitle("Water Reminder 💧")
            .setContentText(reminder)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)

        // Set custom sound if not system default (index 5)
        val soundResId = when (soundIndex) {
            0 -> R.raw.water_drop_1
            1 -> R.raw.water_drop_2
            2 -> R.raw.water_drop_3
            3 -> R.raw.water_drop_4
            4 -> R.raw.water_drop_5
            else -> null
        }

        if (soundResId != null) {
            val soundUri = "android.resource://${context.packageName}/$soundResId".toUri()
            builder.setSound(soundUri)
        }
        // If null (system default), NotificationCompat uses the channel default sound

        notificationManager.notify(1234, builder.build())
    }
}