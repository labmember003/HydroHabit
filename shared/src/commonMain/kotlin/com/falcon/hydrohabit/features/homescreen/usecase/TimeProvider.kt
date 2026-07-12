package com.falcon.hydrohabit.features.homescreen.usecase

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class CurrentTimeInfo(
    val hour: Int,
    val minute: Int,
    val dateString: String,
    val dayOfMonth: Int
)

fun currentTimeInfo(): CurrentTimeInfo {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return CurrentTimeInfo(
        hour = now.hour,
        minute = now.minute,
        dateString = now.date.toString(),
        dayOfMonth = now.dayOfMonth
    )
}
