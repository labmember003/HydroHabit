package com.falcon.hydrohabit.features.onboarding.source

import com.falcon.hydrohabit.features.homescreen.utils.StreakClass
import com.falcon.hydrohabit.features.homescreen.utils.StreakMonthClass
import com.falcon.hydrohabit.features.homescreen.utils.UserSettings
import com.falcon.hydrohabit.features.homescreen.utils.UserValues
import com.falcon.hydrohabit.features.homescreen.utils.WaterAmount
import kotlinx.coroutines.flow.Flow

/**
 * Platform-independent contract for the onboarding/storage repository.
 * Android implements this with DataStore; iOS will implement with its own storage.
 */
interface OnboardingRepositoryContract {

    // Streak
    suspend fun updateStreak(streak: Int, streakDays: List<Int>, streakDay: String, waterTime: String, perks: List<Int>)
    fun getStreak(): Flow<StreakClass>

    // Streak Month
    suspend fun updateStreakMonth(streakDay: Int, streakMonth: Int, streakYear: Int)
    fun getStreakMonth(): Flow<StreakMonthClass>

    // User Settings
    suspend fun updateUserSettingsStore(userHeight: Int, userWaterIntake: Int, userName: String, userWeight: Int, onBoardingCompleted: Boolean)
    fun getUserSettingsStore(): Flow<UserSettings>

    // Water Amount
    suspend fun updateWaterAmount(onUsedWater: Int, onTotalWaterAmount: Int, onWaterDay: String)
    fun removeWaterAmount()
    fun getWaterAmount(): Flow<WaterAmount>

    // User Values
    suspend fun updateUserValues(avgIntake: String, bestStreak: String)
    fun getUserValues(): Flow<UserValues>
}

