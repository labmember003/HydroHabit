package com.falcon.hydrohabit.features.history.model

import kotlinx.serialization.Serializable

/**
 * One logged drink.
 *
 * @param date the local calendar day (yyyy-MM-dd) the drink was logged on.
 * @param healthId identifier of the matching sample in Apple Health, when synced.
 */
@Serializable
data class IntakeEntry(
    val id: String,
    val epochMillis: Long,
    val date: String,
    val amountMl: Int,
    val healthId: String? = null,
)

/**
 * Every drink the user has logged, plus the daily goal that applied on each day,
 * so history keeps showing old days against the goal they had at the time.
 */
@Serializable
data class IntakeLog(
    val entries: List<IntakeEntry> = emptyList(),
    val goals: Map<String, Int> = emptyMap(),
)
