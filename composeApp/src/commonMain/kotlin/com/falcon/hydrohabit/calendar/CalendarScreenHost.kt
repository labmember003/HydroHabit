package com.falcon.hydrohabit.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.falcon.hydrohabit.ui.theme.backgroundColor1
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor
import com.falcon.hydrohabit.features.calendarscreen.usecase.CalendarInfoProvider
import com.falcon.hydrohabit.features.calendarscreen.usecase.calculateBestStreak
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract

@Composable
fun CalendarScreenHost(
    onboardingRepo: OnboardingRepositoryContract
) {
    var onMonth by remember { mutableStateOf("") }
    var onDays by remember { mutableIntStateOf(0) }
    val streakDays = remember { mutableStateListOf<Int>() }
    var onStreakDays by remember { mutableIntStateOf(0) }
    val calendarList = remember { mutableStateListOf<List<Color>>() }

    var avgWaterIntake by remember { mutableStateOf("") }
    var bestStreak by remember { mutableIntStateOf(0) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }

    fun getCalendarValues() {
        onMonth = CalendarInfoProvider.getCurrentMonthName()
        onDays = CalendarInfoProvider.getDaysInCurrentMonth()
        calendarList.clear()
        for (i in 0 until 5) {
            val rowList = mutableListOf<Color>()
            for (j in 0 until 7) {
                val count = i * 7 + j
                if (count < onDays) {
                    if (streakDays.isNotEmpty() && streakDays.contains(count)) {
                        rowList.add(waterColor)
                    } else {
                        rowList.add(primaryBlack)
                    }
                }
            }
            calendarList.add(rowList)
        }
    }

    LaunchedEffect(Unit) {
        onboardingRepo.getStreak().collect { streak ->
            onStreakDays = streak.streak
            streakDays.clear()
            streakDays.addAll(streak.streakDays)
            getCalendarValues()

            val newBest = calculateBestStreak(currentBest = bestStreak, currentStreak = onStreakDays)
            bestStreak = newBest
            onboardingRepo.updateUserValues(avgIntake = "1000", bestStreak = bestStreak.toString())
        }
    }

    LaunchedEffect(Unit) {
        onboardingRepo.getUserValues().collect {
            avgWaterIntake = it.avgIntake
            bestStreak = if (it.bestStreak.isEmpty()) 0 else it.bestStreak.toInt()
        }
    }

    LaunchedEffect(Unit) {
        onboardingRepo.getUserSettingsStore().collect {
            height = it.userHeight.toString()
            weight = it.userWeight.toString()
        }
    }

    CalendarScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    start = Offset(Float.POSITIVE_INFINITY, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                    colors = listOf(backgroundColor1, backgroundColor2)
                )
            ),
        onMonth = onMonth,
        calendarList = calendarList.toList(),
        onAvgIntake = avgWaterIntake,
        onBestStreak = bestStreak.toString(),
        onWeight = weight,
        onHeight = height,
        intakeAmount = avgWaterIntake
    )
}
