package com.falcon.hydrohabit.model.water_reminder

import kotlinx.datetime.LocalDateTime

data class WaterReminder(
    val time: LocalDateTime,
    val message: String
)

