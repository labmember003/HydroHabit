package com.falcon.hydrohabit.composeapp.alarm

import com.falcon.hydrohabit.model.water_reminder.WaterReminder

interface AlarmSchedulerContract {
    fun schedule(reminder: WaterReminder)
    fun cancel(reminder: WaterReminder)
    fun scheduleRepeating(
        intervalMinutes: Int,
        wakeUpHour: Int = 8,
        wakeUpMinute: Int = 0,
        bedHour: Int = 22,
        bedMinute: Int = 0
    )
    fun cancelAll()
}
