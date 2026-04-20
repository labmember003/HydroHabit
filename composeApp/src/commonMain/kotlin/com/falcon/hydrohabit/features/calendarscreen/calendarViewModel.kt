package com.falcon.hydrohabit.features.calendarscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.hydrohabit.features.calendarscreen.utils.WaterGoals
import com.falcon.hydrohabit.features.homescreen.utils.StreakClass
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepository
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CalendarViewModel(private val onboardingRepo: OnboardingRepository) : ViewModel() {

    var onMonth by mutableStateOf("")
        private set

    private var _onWaterGoals = mutableStateListOf<WaterGoals>()
    var onWaterGoals: List<WaterGoals> = _onWaterGoals

    var onDays by mutableIntStateOf(0)
        private set

    var streaKDays = mutableStateListOf<Int>()
        private set
    var _streak by mutableStateOf(StreakClass())
        private set
    var onStreakDays by mutableIntStateOf(0)
        private set

    var calendarList: MutableList<List<Color>> = mutableListOf()
        private set

    var avgWaterIntake by mutableStateOf("")
        private set
    var bestStreak by mutableStateOf(0)
        private set
    var weight by mutableStateOf("")
        private set
    var height by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            onboardingRepo.initialize()
            getStreakScore()
        }
        viewModelScope.launch { getUserSettings() }
        viewModelScope.launch { getUserValues() }
    }

    fun updateWaterGoals(index: Int) {
        _onWaterGoals[index] = _onWaterGoals[index].copy(onSelected = !_onWaterGoals[index].onSelected)
    }

    private suspend fun getStreakScore() {
        onboardingRepo.getStreak().collect {
            _streak = it
            onStreakDays = it.streak
            streaKDays.addAll(it.streakDays)
            getCalendarValues()
            calculateUserValues()
        }
    }

    private suspend fun calculateUserValues() {
        if (onStreakDays == 0) {
            bestStreak = 0
        } else {
            bestStreak = onStreakDays
        }
    }

    private fun getCalendarValues() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val month = now.monthNumber
        val year = now.year

        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        onMonth = months[month - 1]

        // Simple calendar grid generation
        val daysInMonth = when (month) {
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
        onDays = daysInMonth

        calendarList.clear()
        val rows = mutableListOf<List<Color>>()
        var currentRow = mutableListOf<Color>()
        for (day in 1..daysInMonth) {
            val color = if (streaKDays.contains(day)) waterColor else primaryBlack
            currentRow.add(color)
            if (currentRow.size == 7) {
                rows.add(currentRow.toList())
                currentRow = mutableListOf()
            }
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow.toList())
        }
        calendarList.addAll(rows)
    }

    private suspend fun getUserSettings() {
        onboardingRepo.getUserSettingsStore().collect {
            weight = it.userWeight.toString()
            height = it.userHeight.toString()
        }
    }

    private suspend fun getUserValues() {
        onboardingRepo.getUserValues().collect {
            avgWaterIntake = it.avgIntake
        }
    }
}

