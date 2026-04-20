package com.falcon.hydrohabit.features.onboarding.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.hydrohabit.features.homescreen.utils.UserSettings
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepository
import com.falcon.hydrohabit.platform.KVStorage
import com.falcon.hydrohabit.platform.currentDateString
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class OnboardingViewModel(
    private val onboardingRepo: OnboardingRepository,
    private val kvStorage: KVStorage
) : ViewModel() {

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
        viewModelScope.launch {
            kvStorage.putInt("wake_up_hour", wakeUpHour)
            kvStorage.putInt("bed_hour", bedHour)
        }
    }

    init {
        viewModelScope.launch {
            onboardingRepo.initialize()
            getUserSettings()
        }
    }

    fun updatePermissionNotification(permission: Boolean) {
        onNotificationPermissionDenied = permission
    }

    fun checkBodyMeasurementsFields() {
        onWeightCheck = onWeightValue.isBlank()
        onHeightCheck = onHeightValue.isBlank()
        if (onWeightCheck) onWeightError = "Please Enter the Weight"
        if (onHeightCheck) onHeightError = "Please Enter the Height"
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
            0 -> 50
            1 -> 35
            else -> 20
        }
    }

    fun checkActivtyLevels() {
        onActivityLevelCheck = onActiveValue == null
        onActivityLevelError = if (onActiveValue == null) "Please Add your Activity Levels" else ""
    }

    fun calculateWaterIntake() {
        BSA = sqrt(onHeightValue.toInt().times(onWeightValue.toDouble() / 3600)).toInt()
        BWI = onWeightValue.toInt().times(33)
        val BWI_adjusted = BWI * BSA
        TWI = BWI_adjusted + (BWI_adjusted.times(activityLevel ?: 0) / 100).toDouble()

        TWI = when {
            TWI > 3700 -> 3700.0
            TWI > 3000 && TWI < 3500 -> 3500.0
            TWI > 2500 && TWI < 3000 -> 3000.0
            TWI > 2000 && TWI < 2500 -> 2500.0
            TWI < 2000 -> 2000.0
            else -> TWI
        }

        onWaterAmount = TWI.toInt()
        viewModelScope.launch {
            calculateWaterAmount()
        }
        TWI /= 1000
    }

    private suspend fun calculateWaterAmount() {
        val date = currentDateString()
        onboardingRepo.updateWaterAmount(
            onUsedWater = 0,
            onTotalWaterAmount = onWaterAmount,
            onWaterDay = date
        )
    }

    fun updateUserSettings(onBoardingCompleted: Boolean) {
        viewModelScope.launch {
            onboardingRepo.updateUserSettingsStore(
                userWeight = onWeightValue.toInt(),
                userWaterIntake = onWaterAmount,
                userName = onNameValue,
                userHeight = onHeightValue.toInt(),
                onBoardingCompleted = onBoardingCompleted
            )
            kvStorage.putBoolean("onBoardingCompleted", onBoardingCompleted)
        }
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
        }
    }
}

