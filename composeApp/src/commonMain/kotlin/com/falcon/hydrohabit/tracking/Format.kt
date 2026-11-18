package com.falcon.hydrohabit.tracking

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

/** 2140 -> "2,140 ml" */
fun formatMl(ml: Int): String = "${groupThousands(ml)} ml"

fun groupThousands(value: Int): String {
    val digits = kotlin.math.abs(value).toString()
    val grouped = digits.reversed().chunked(3).joinToString(",").reversed()
    return if (value < 0) "-$grouped" else grouped
}

/** 13:05 -> "1:05 PM" */
fun formatClock(hour: Int, minute: Int): String {
    val h12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val suffix = if (hour < 12) "AM" else "PM"
    return "$h12:${minute.toString().padStart(2, '0')} $suffix"
}

fun Month.displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }

fun Month.shortName(): String = displayName().take(3)

fun DayOfWeek.shortName(): String = name.lowercase().replaceFirstChar { it.uppercase() }.take(3)

/** "Wednesday, Nov 18" */
fun LocalDate.longLabel(): String =
    "${dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, ${month.shortName()} $dayOfMonth"
