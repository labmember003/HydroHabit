package com.falcon.hydrohabit.features.onboarding.viewModel

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import com.falcon.hydrohabit.features.onboarding.usecase.WaterIntakeCalculator
import com.falcon.hydrohabit.features.homescreen.utils.UserSettings
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.core.content.edit

class OnboardingViewModel(private val onboardingRepo: OnboardingRepositoryContract, private val sharedPreferences: SharedPreferences) : ViewModel() {

    var onNameValue by mutableStateOf("")
        private set
    var onWeightValue by mutableStateOf("")
        private set
    var onWaterAmount by mutableIntStateOf(0)
        private set
    var onHeightValue by mutableStateOf("")
        private set
    var onActiveValue by mutableStateOf<Int?>(null)
        private set
    var onWeightCheck by mutableStateOf(false)
        private set
    var onHeightCheck by mutableStateOf(false)
        private set
    var onWeightError by mutableStateOf("")
        private set
    var onHeightError by mutableStateOf("")
        private set
    var onboardingCompleted by mutableStateOf(false)
        private set
    var _userSettings by mutableStateOf(UserSettings())
        private set
    var checkDigit by mutableStateOf(false)
        private set
    var BSA by mutableStateOf(0)
        private set
    var BWI by mutableStateOf(0)
        private set
    var TWI by mutableStateOf(0.0)
        private set
    var activityLevel by mutableStateOf<Int?>(null)
        private set
    var onActivityLevelCheck by mutableStateOf(false)
        private set
    var onActivityLevelError by mutableStateOf("")
        private set

    var onNotificationPermissionDenied by mutableStateOf<Boolean?>(null)
        private set

    var wakeUpHour by mutableIntStateOf(8)
        private set
    var bedHour by mutableIntStateOf(22)
        private set

    fun updateWakeUpHour(hour: Int) {
        wakeUpHour = hour
    }

    fun updateBedHour(hour: Int) {
        bedHour = hour
    }

    fun saveSleepSchedule() {
        sharedPreferences.edit {
            putInt("wake_up_hour", wakeUpHour)
            putInt("bed_hour", bedHour)
        }
    }


    init {
        viewModelScope.launch {
//        calculateStreakScore()
//        getStreakScore()
//        calculateStreakMonthScore()
//        getMonthStreak()
            getUserSettings()
        }

    }

    fun updatePermissionNotification(permission:Boolean){
        onNotificationPermissionDenied = permission
    }

    fun checkBodyMeasurementsFields() {
        onWeightCheck = onWeightValue.isBlank()
        onHeightCheck = onHeightValue.isBlank()
        if (onWeightCheck) {
            onWeightError = "Please Enter the Weight"
        }
        if (onHeightCheck) {
            onHeightError = "Please Enter the Height"
        }
    }

    fun updateOnboardingWaterAmount(value: Int) {
        onWaterAmount = value
    }

    fun onWeightChange(getWeightValue: String) {
        onWeightValue = getWeightValue.filter { it.isDigit() }
    }

    fun onHeightChange(getHeightValue: String) {
        onHeightValue = getHeightValue.filter { it.isDigit() }
    }

    fun onBoardingActiveScreen(getActiveValue: Int) {
        onActiveValue = getActiveValue
        activityLevel = when (onActiveValue) {
            0 -> {
                50
            }

            1 -> {
                35
            }

            else -> {
                20
            }
        }
    }

    fun checkActivtyLevels() {
        onActivityLevelCheck = onActiveValue == null
        onActivityLevelError = if (onActiveValue == null) "Please Add your Activity Levels" else ""
    }

    fun calculateWaterIntake() {
        onWaterAmount = WaterIntakeCalculator.calculateWaterIntake(
            heightCm = onHeightValue.toInt(),
            weightKg = onWeightValue.toInt(),
            activityLevel = activityLevel ?: 0
        )

        viewModelScope.launch {
            calculateWaterAmount()
        }
        println("onWaterAmount Onboarding: ${onWaterAmount}")
        TWI = onWaterAmount.toDouble() / 1000
        println("TWI Onboarding: ${"%.2f".format(TWI).toDouble()}")
    }

    // Update the water Amount in database
    private suspend fun calculateWaterAmount() {
        val date = LocalDate.now()
        onboardingRepo.updateWaterAmount(
            onUsedWater = 0,
            onTotalWaterAmount = onWaterAmount,
            onWaterDay = date.toString()
        )
        println("Date onWaterDay ${date.toString()}")

    }


    fun updateUserSettings(onBoardingCompleted:Boolean) {

        viewModelScope.launch {
            onboardingRepo.updateUserSettingsStore(
                userWeight = onWeightValue.toInt(),
                userWaterIntake = onWaterAmount,
                userName = onNameValue,
                userHeight = onHeightValue.toInt(),
                onBoardingCompleted = onBoardingCompleted
            )
        }

        sharedPreferences.edit{ putBoolean("onBoardingCompleted", onBoardingCompleted) }

        println("streakScore Onboarding UpdateUserSettings ")

    }


    private suspend fun getUserSettings() {
        onboardingRepo.getUserSettingsStore().collect {
            _userSettings = it
            onNameValue = it.userName
            if (it.userWeight != 0 && it.userHeight != 0) {
                onWeightValue = it.userWeight.toString()
                onHeightValue = it.userHeight.toString()
            } else {
                onWeightValue = ""
                onHeightValue = ""
            }
            TWI = it.userWaterIntake.toDouble()
            println("streakScore Onboarding getUserSettings ${it}")
        }

    }


}