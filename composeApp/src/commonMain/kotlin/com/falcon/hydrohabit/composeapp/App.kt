package com.falcon.hydrohabit.composeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.composeapp.onboarding.screens.OnBoardingActiveScreen
import com.falcon.hydrohabit.composeapp.onboarding.screens.OnBoardingBodyMeasurementsScreen
import com.falcon.hydrohabit.composeapp.onboarding.screens.OnBoardingSleepScheduleScreen
import com.falcon.hydrohabit.composeapp.onboarding.screens.OnBoardingWaterIntakeResultScreen
import com.falcon.hydrohabit.composeapp.ui.theme.HydroHabitTheme
import com.falcon.hydrohabit.composeapp.ui.theme.backgroundColor1
import com.falcon.hydrohabit.composeapp.ui.theme.backgroundColor2
import com.falcon.hydrohabit.composeapp.ui.theme.primaryBlack
import com.falcon.hydrohabit.composeapp.ui.theme.waterColor
import com.falcon.hydrohabit.composeapp.ui.theme.waterColorBackground
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.usecase.WaterIntakeCalculator
import com.falcon.hydrohabit.features.onboarding.utils.BodyMeasurementData
import com.falcon.hydrohabit.features.onboarding.utils.activityMeasurementData

private enum class OnboardingStep {
    BodyMeasurement,
    Activity,
    WaterResult,
    SleepSchedule,
}

/**
 * Root composable for the Compose Multiplatform app.
 */
@Composable
fun App(appPrefsRepo: AppPreferencesRepository) {
    val prefs by appPrefsRepo.cachedPreferences.collectAsState()

    HydroHabitTheme {
        if (prefs.onboardingCompleted) {
            // Home screen placeholder — will be migrated in future phase
            HomeScreenPlaceholder()
        } else {
            OnboardingFlow(appPrefsRepo)
        }
    }
}

@Composable
private fun HomeScreenPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor1)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "HydroHabit 💧",
            style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold, color = primaryBlack)
        )
        Text(
            text = "Welcome back!",
            style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, color = waterColor),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun OnboardingFlow(appPrefsRepo: AppPreferencesRepository) {
    var currentStep by remember { mutableStateOf(OnboardingStep.BodyMeasurement) }

    // Body measurement state
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf(false) }
    var heightError by remember { mutableStateOf(false) }

    // Activity state
    var activityLevel by remember { mutableIntStateOf(-1) }
    var activityError by remember { mutableStateOf(false) }

    // Water intake result
    var waterIntake by remember { mutableStateOf("") }

    // Sleep schedule
    var wakeUpHour by remember { mutableIntStateOf(8) }
    var bedHour by remember { mutableIntStateOf(22) }

    val backgroundModifier = Modifier
        .fillMaxSize()
        .background(
            Brush.linearGradient(
                start = Offset(Float.POSITIVE_INFINITY * 0.4f, 0f),
                end = Offset(0f, Float.POSITIVE_INFINITY),
                colors = listOf(waterColorBackground, backgroundColor2)
            )
        )

    when (currentStep) {
        OnboardingStep.BodyMeasurement -> {
            OnBoardingBodyMeasurementsScreen(
                modifier = backgroundModifier,
                bodyMeasurementData = BodyMeasurementData(
                    onWeightChange = weight,
                    onHeightChange = height,
                    onWeightCheck = weightError,
                    onHeightCheck = heightError,
                    onWeightError = "Please enter a valid weight",
                    onHeightError = "Please enter a valid height"
                ),
                getWeightChange = { weight = it; weightError = false },
                getHeightChange = { height = it; heightError = false },
                getNavigate = {
                    val w = weight.toFloatOrNull()
                    val h = height.toFloatOrNull()
                    weightError = w == null || w <= 0f || w > 300f
                    heightError = h == null || h <= 0f || h > 300f
                    if (!weightError && !heightError) {
                        currentStep = OnboardingStep.Activity
                    }
                }
            )
        }

        OnboardingStep.Activity -> {
            OnBoardingActiveScreen(
                modifier = backgroundModifier,
                activityMeasurementData = activityMeasurementData(
                    onActivityOutcome = activityLevel,
                    onErrorText = "Please select your activity level",
                    checkError = activityError
                ),
                getActiveOutcome = { activityLevel = it; activityError = false },
                getNavigate = {
                    if (activityLevel < 0) {
                        activityError = true
                    } else {
                        val h = height.toFloatOrNull() ?: 170f
                        val w = weight.toFloatOrNull() ?: 70f
                        waterIntake = "${WaterIntakeCalculator.calculateWaterIntake(h.toInt(), w.toInt(), activityLevel)} ml"
                        currentStep = OnboardingStep.WaterResult
                    }
                },
                getBacK = { currentStep = OnboardingStep.BodyMeasurement }
            )
        }

        OnboardingStep.WaterResult -> {
            OnBoardingWaterIntakeResultScreen(
                modifier = backgroundModifier,
                onWaterIntake = waterIntake,
                getNavigate = { currentStep = OnboardingStep.SleepSchedule },
                getBack = { currentStep = OnboardingStep.Activity }
            )
        }

        OnboardingStep.SleepSchedule -> {
            OnBoardingSleepScheduleScreen(
                modifier = backgroundModifier,
                selectedWakeUpHour = wakeUpHour,
                selectedBedHour = bedHour,
                getWakeUpChange = { wakeUpHour = it },
                getBedTimeChange = { bedHour = it },
                getNavigate = {
                    // Onboarding complete — save to DataStore
                    // In a real app this would be in a coroutine scope
                    // For now just mark as completed
                },
                getBack = { currentStep = OnboardingStep.WaterResult }
            )
        }
    }
}
