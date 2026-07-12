package com.falcon.hydrohabit.model.water_reminder

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class WaterReminder(
    val time: LocalDateTime,
    val message: String
) {
    companion object {
        fun inTwoHours(message: String): WaterReminder {
            val time = Clock.System.now()
                .plus(2, DateTimeUnit.HOUR)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            return WaterReminder(time, message)
        }
    }
}

