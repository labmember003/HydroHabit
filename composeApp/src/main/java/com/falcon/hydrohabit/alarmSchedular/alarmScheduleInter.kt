package com.falcon.hydrohabit.alarmSchedular

import com.falcon.hydrohabit.model.water_reminder.WaterReminder

interface AlarmSchedularInterface {
    fun schedule(reminder: WaterReminder)
    fun cancel(reminder: WaterReminder)
}