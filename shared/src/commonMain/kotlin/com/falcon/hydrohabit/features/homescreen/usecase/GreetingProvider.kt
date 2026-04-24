package com.falcon.hydrohabit.features.homescreen.usecase

/**
 * Pure function that returns a time-of-day greeting.
 * Extracted from HomeViewModel for KMM sharing.
 *
 * @param hourOfDay the current hour in 24-hour format (0–23)
 * @return a greeting string
 */
fun getGreeting(hourOfDay: Int): String {
    return when (hourOfDay) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else -> "Good Night"
    }
}

