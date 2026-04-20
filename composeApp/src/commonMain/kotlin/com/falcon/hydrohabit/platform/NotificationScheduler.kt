package com.falcon.hydrohabit.platform

/**
 * Multiplatform notification scheduler interface.
 */
expect class NotificationScheduler {
    fun scheduleRepeating(
        intervalMinutes: Int,
        wakeUpHour: Int,
        wakeUpMinute: Int,
        bedHour: Int,
        bedMinute: Int
    )
    fun cancelAll()
    fun showNotification(title: String, message: String)
}

