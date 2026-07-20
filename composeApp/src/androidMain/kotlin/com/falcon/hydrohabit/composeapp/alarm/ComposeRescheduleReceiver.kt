package com.falcon.hydrohabit.composeapp.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.falcon.hydrohabit.composeapp.settings.intervalMinutesMap
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// Re-arms all water reminders whenever the alarm set is lost or stale: after reboot,
// app update, wall-clock/timezone changes, the exact-alarm grant changing, and the
// daily self-refresh (alarms are one-shot and only cover one day).
class ComposeRescheduleReceiver : BroadcastReceiver(), KoinComponent {

    private val appPrefsRepo: AppPreferencesRepository by inject()
    private val alarmScheduler: AlarmSchedulerContract by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            ACTION_REARM,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> Unit
            else -> return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val prefs = appPrefsRepo.current()
                if (prefs.notificationsEnabled) {
                    val safeIndex = prefs.notificationIntervalIndex.coerceIn(intervalMinutesMap.indices)
                    alarmScheduler.scheduleRepeating(
                        intervalMinutesMap[safeIndex],
                        prefs.wakeUpHour,
                        prefs.wakeUpMinute,
                        prefs.bedHour,
                        prefs.bedMinute,
                        prefs.notificationSoundIndex
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_REARM = "com.falcon.hydrohabit.composeapp.action.REARM_REMINDERS"
    }
}
