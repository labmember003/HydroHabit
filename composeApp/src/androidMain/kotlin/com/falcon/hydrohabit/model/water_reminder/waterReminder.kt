package com.falcon.hydrohabit.model.water_reminder

import java.time.LocalDateTime

data class WaterReminder(
    val time:LocalDateTime,
    val message:String
)
