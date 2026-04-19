package com.falcon.hydrohabit.features.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import androidx.core.app.NotificationCompat
import com.falcon.hydrohabit.MainActivity
import com.falcon.hydrohabit.R
import com.falcon.hydrohabit.receiver.SnoozeReceiver
import androidx.core.net.toUri

class NotificationChannelService(
    private val context: Context
) : NotificationChannelInterface {

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID_PREFIX = "water_reminder_sound_"
    }

    private fun getChannelId(soundIndex: Int): String = "$CHANNEL_ID_PREFIX$soundIndex"

    private fun getSoundUri(soundIndex: Int): android.net.Uri? {
        val soundResId = when (soundIndex) {
            0 -> R.raw.water_drop_1
            1 -> R.raw.water_drop_2
            2 -> R.raw.water_drop_3
            3 -> R.raw.water_drop_4
            4 -> R.raw.water_drop_5
            else -> null
        }
        return soundResId?.let { "android.resource://${context.packageName}/$it".toUri() }
    }

    private fun ensureChannel(soundIndex: Int) {
        val channelId = getChannelId(soundIndex)
        if (notificationManager.getNotificationChannel(channelId) != null) return

        val soundUri = getSoundUri(soundIndex)
        val channelName = when (soundIndex) {
            0 -> "Water Reminder - Droplet"
            1 -> "Water Reminder - Ripple"
            2 -> "Water Reminder - Stream"
            3 -> "Water Reminder - Cascade"
            4 -> "Water Reminder - Ocean"
            else -> "Water Reminder - System Default"
        }
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Reminds user to drink water"
            if (soundUri != null) {
                setSound(soundUri, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
            }
        }
        notificationManager.createNotificationChannel(channel)
    }

    override fun showNotification(reminder: String) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val soundIndex = prefs.getInt("notification_sound_index", 0)

        val channelId = getChannelId(soundIndex)
        ensureChannel(soundIndex)

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("open_add_water", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val activityPendingIntent = PendingIntent.getActivity(
            context, 1234, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Drink action — opens app with add water dialog
        val drinkIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("open_add_water", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val drinkPendingIntent = PendingIntent.getActivity(
            context, 1235, drinkIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze action — reschedules 5 min later
        val snoozeIntent = Intent(context, SnoozeReceiver::class.java)
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, 1236, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Water Reminder 💧")
            .setContentText(reminder)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .addAction(0, "Drink", drinkPendingIntent)
            .addAction(0, "Snooze 5m", snoozePendingIntent)

        notificationManager.notify(1001, builder.build())
    }
}