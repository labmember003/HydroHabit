package com.falcon.hydrohabit.features.homescreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.hydrohabit.features.homescreen.utils.StreakClass
import com.falcon.hydrohabit.features.homescreen.utils.WaterAmount
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepository
import com.falcon.hydrohabit.platform.NotificationScheduler
import com.falcon.hydrohabit.platform.currentDateString
import com.falcon.hydrohabit.platform.currentHourOfDay
import com.falcon.hydrohabit.platform.currentMinute
import com.falcon.hydrohabit.platform.currentDayOfMonth
import com.falcon.hydrohabit.platform.logDebug
import com.falcon.hydrohabit.platform.logError
import com.falcon.hydrohabit.utils.Utils
import kotlinx.coroutines.launch

class HomeViewModel(
    private val onboardingRepo: OnboardingRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    var usedWaterAmount by mutableIntStateOf(0)
        private set

    var totalWaterAmount by mutableIntStateOf(0)
        private set

    var rewardDialog by mutableStateOf(false)
        private set

    var streakDays = mutableStateListOf<Int>()
        private set
    var waterPercent by mutableIntStateOf(0)
        private set
    var _streak by mutableStateOf(StreakClass())
        private set
    var _waterAmount by mutableStateOf(WaterAmount())
        private set
    var perks = mutableStateListOf<Int>()
        private set
    var streakScore by mutableIntStateOf(0)
        private set
    var streakDay by mutableStateOf("")
        private set
    var waterTime by mutableStateOf(24)
        private set
    var onProgress by mutableStateOf("")
        private set
    var onTime by mutableStateOf("")
        private set

    fun DismissReward(reward: Boolean) {
        rewardDialog = reward
    }

    init {
        viewModelScope.launch {
            onboardingRepo.initialize()
            getWaterAmount()
        }
        viewModelScope.launch {
            getStreakScore()
        }
        waterStreak()
    }

    fun updateTotalWaterAmount(totalWater: Int) {
        totalWaterAmount = totalWater
    }

    fun getGreeting() {
        val timeOfDay = currentHourOfDay()
        onTime = when (timeOfDay) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }

    fun fillWaterUpdate(waterUpdate: Int) {
        val currentMinuteVal = currentMinute()
        val date = currentDateString()
        val dayOfMonth = currentDayOfMonth()

        if (waterPercent < 100) {
            waterTime = currentMinuteVal
            usedWaterAmount += waterUpdate
            waterPercent = usedWaterAmount * 100 / totalWaterAmount
        } else if (waterPercent >= 100) {
            rewardDialog = true
            if (date != streakDay) {
                streakScore++
                streakDays.addAll(streakDays)
                streakDays.add(dayOfMonth - 1)
                viewModelScope.launch {
                    calculateStreakScore()
                }
            }
            streakDay = date
        } else {
            waterPercent = 100
        }

        viewModelScope.launch {
            calculateWaterAmount()
        }
        waterStreak()
    }

    private suspend fun calculateWaterAmount() {
        val date = currentDateString()
        onboardingRepo.updateWaterAmount(
            onUsedWater = usedWaterAmount,
            onTotalWaterAmount = totalWaterAmount,
            onWaterDay = date
        )
    }

    private suspend fun calculateStreakScore() {
        onboardingRepo.updateStreak(
            streak = streakScore,
            streakDay = streakDay,
            streakDays = streakDays,
            waterTime = waterTime.toString(),
            perks = perks
        )
    }

    private suspend fun getWaterAmount() {
        val date = currentDateString()
        onboardingRepo.getWaterAmount().collect {
            _waterAmount = it
            usedWaterAmount = it.onUsedWater
            totalWaterAmount = it.onTotalWater

            if (it.onUsedWater > 0) {
                waterPercent = if (it.onUsedWater * 100 / it.onTotalWater.coerceAtLeast(1) <= 100) {
                    it.onUsedWater * 100 / it.onTotalWater.coerceAtLeast(1)
                } else {
                    100
                }
            }
            if (date != it.onWaterDay) {
                usedWaterAmount = 0
                waterPercent = 0
                calculateWaterAmount()
            }
        }
    }

    private suspend fun getStreakScore() {
        onboardingRepo.getStreak().collect { it ->
            _streak = it
            streakScore = it.streak
            if (it.perks.isNotEmpty()) {
                perks.clear()
                it.perks.forEach {
                    if (!perks.contains(it)) {
                        perks.add(it)
                    }
                }
            }
            if (it.waterTime != "") {
                waterTime = it.waterTime.toIntOrNull() ?: 24
            }
            streakDay = it.streakDay
            if (it.streakDays.isNotEmpty()) {
                streakDays.addAll(it.streakDays)
            }
        }
    }

    fun waterStreak() {
        when (waterPercent) {
            in 0..30 -> onProgress = "Keep Going, You are doing Great"
            in 30..50 -> onProgress = "You are half way through, keep it up"
            in 80..95 -> onProgress = "You are almost there, keep it going "
            in 95..100 -> onProgress = "Amazing"
        }
    }
}

