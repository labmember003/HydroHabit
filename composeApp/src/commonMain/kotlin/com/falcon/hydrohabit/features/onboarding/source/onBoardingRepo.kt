package com.falcon.hydrohabit.features.onboarding.source

import com.falcon.hydrohabit.features.homescreen.utils.StreakClass
import com.falcon.hydrohabit.features.homescreen.utils.StreakMonthClass
import com.falcon.hydrohabit.features.homescreen.utils.UserSettings
import com.falcon.hydrohabit.features.homescreen.utils.UserValues
import com.falcon.hydrohabit.features.homescreen.utils.WaterAmount
import com.falcon.hydrohabit.platform.JsonFileStorage
import com.falcon.hydrohabit.platform.read
import com.falcon.hydrohabit.platform.write
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingRepository {
    private val streakStorage = JsonFileStorage("streak_store.json")
    private val streakMonthStorage = JsonFileStorage("streak_store_month.json")
    private val userSettingsStorage = JsonFileStorage("user_settings_store.json")
    private val waterAmountStorage = JsonFileStorage("water_amount.json")
    private val userValuesStorage = JsonFileStorage("user_values.json")

    // In-memory state flows that emit updated values
    private val _streak = MutableStateFlow(StreakClass())
    private val _streakMonth = MutableStateFlow(StreakMonthClass())
    private val _userSettings = MutableStateFlow(UserSettings())
    private val _waterAmount = MutableStateFlow(WaterAmount())
    private val _userValues = MutableStateFlow(UserValues())

    private var initialized = false

    suspend fun initialize() {
        if (initialized) return
        _streak.value = streakStorage.read(StreakClass())
        _streakMonth.value = streakMonthStorage.read(StreakMonthClass())
        _userSettings.value = userSettingsStorage.read(UserSettings())
        _waterAmount.value = waterAmountStorage.read(WaterAmount())
        _userValues.value = userValuesStorage.read(UserValues())
        initialized = true
    }

    suspend fun updateUserValues(avgIntake: String, bestStreak: String) {
        val updated = _userValues.value.copy(bestStreak = bestStreak, avgIntake = avgIntake)
        _userValues.value = updated
        userValuesStorage.write(updated)
    }

    fun getUserValues(): Flow<UserValues> = _userValues.asStateFlow()

    suspend fun updateStreak(streak: Int, streakDays: List<Int>, streakDay: String, waterTime: String, perks: List<Int>) {
        val updated = _streak.value.copy(
            streak = streak,
            streakDays = streakDays,
            streakDay = streakDay,
            waterTime = waterTime,
            perks = perks
        )
        _streak.value = updated
        streakStorage.write(updated)
    }

    fun getStreak(): Flow<StreakClass> = _streak.asStateFlow()

    suspend fun updateStreakMonth(streakDay: Int, streakMonth: Int, streakYear: Int) {
        val updated = _streakMonth.value.copy(
            streakDay = streakDay,
            streakMonth = streakMonth,
            streakYear = streakYear
        )
        _streakMonth.value = updated
        streakMonthStorage.write(updated)
    }

    fun getStreakMonth(): Flow<StreakMonthClass> = _streakMonth.asStateFlow()

    suspend fun updateUserSettingsStore(userHeight: Int, userWaterIntake: Int, userName: String, userWeight: Int, onBoardingCompleted: Boolean) {
        val updated = _userSettings.value.copy(
            userHeight = userHeight,
            userName = userName,
            userWaterIntake = userWaterIntake,
            userWeight = userWeight,
            registrationCompleted = onBoardingCompleted
        )
        _userSettings.value = updated
        userSettingsStorage.write(updated)
    }

    fun getUserSettingsStore(): Flow<UserSettings> = _userSettings.asStateFlow()

    suspend fun updateWaterAmount(onUsedWater: Int, onTotalWaterAmount: Int, onWaterDay: String) {
        val updated = _waterAmount.value.copy(
            onUsedWater = onUsedWater,
            onTotalWater = onTotalWaterAmount,
            onWaterDay = onWaterDay
        )
        _waterAmount.value = updated
        waterAmountStorage.write(updated)
    }

    fun getWaterAmount(): Flow<WaterAmount> = _waterAmount.asStateFlow()
}

