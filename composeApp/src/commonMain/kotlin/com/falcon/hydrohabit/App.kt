package com.falcon.hydrohabit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.falcon.hydrohabit.alarm.AlarmSchedulerContract
import kotlinx.coroutines.launch
import com.falcon.hydrohabit.navigation.MainScreen
import com.falcon.hydrohabit.onboarding.screens.OnBoardingActiveScreen
import com.falcon.hydrohabit.onboarding.screens.OnBoardingBodyMeasurementsScreen
import com.falcon.hydrohabit.onboarding.screens.OnBoardingSleepScheduleScreen
import com.falcon.hydrohabit.onboarding.screens.OnBoardingNotificationScreen
import com.falcon.hydrohabit.onboarding.screens.OnBoardingWaterIntakeResultScreen
import com.falcon.hydrohabit.onboarding.screens.OnboardingLoadingScreen
import com.falcon.hydrohabit.onboarding.components.WeightUnit
import com.falcon.hydrohabit.onboarding.components.toKg
import com.falcon.hydrohabit.ui.theme.HydroHabitTheme
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.waterColorBackground
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import com.falcon.hydrohabit.features.homescreen.usecase.currentTimeInfo
import com.falcon.hydrohabit.features.onboarding.usecase.Gender
import com.falcon.hydrohabit.features.onboarding.usecase.WaterIntakeCalculator
import com.falcon.hydrohabit.features.onboarding.utils.BodyMeasurementData
import com.falcon.hydrohabit.features.onboarding.utils.activityMeasurementData

private enum class OnboardingStep {
    BodyMeasurement,
    Activity,
    Loading,
    WaterResult,
    SleepSchedule,
    NotificationPermission,
}

/**
 * Root composable for the Compose Multiplatform app.
 */
@Composable
fun App(
    appPrefsRepo: AppPreferencesRepository,
    onboardingRepo: OnboardingRepositoryContract,
    alarmScheduler: AlarmSchedulerContract,
    shouldOpenAddWater: Boolean = false,
    onAddWaterHandled: () -> Unit = {}
) {
    val prefs by appPrefsRepo.cachedPreferences.collectAsState()

    HydroHabitTheme {
        if (prefs.onboardingCompleted) {
            MainScreen(
                appPrefsRepo = appPrefsRepo,
                onboardingRepo = onboardingRepo,
                alarmScheduler = alarmScheduler,
                shouldOpenAddWater = shouldOpenAddWater,
                onAddWaterHandled = onAddWaterHandled
            )
        } else {
            OnboardingFlow(appPrefsRepo, onboardingRepo)
        }
    }
}

@Composable
private fun OnboardingFlow(
    appPrefsRepo: AppPreferencesRepository,
    onboardingRepo: OnboardingRepositoryContract
) {
    val scope = rememberCoroutineScope()
    var currentStep by remember { mutableStateOf(OnboardingStep.BodyMeasurement) }

    // Body measurement state
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weightUnit by remember { mutableStateOf(WeightUnit.KG) }
    var gender by remember { mutableStateOf<Gender?>(null) }
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
                weightUnit = weightUnit,
                selectedGender = gender,
                getWeightUnitChange = {
                    weightUnit = it
                    weightError = false
                    scope.launch { appPrefsRepo.setWeightUnit(it.name) }
                },
                getGenderChange = { gender = it },
                getWeightChange = { weight = it; weightError = false },
                getHeightChange = { height = it; heightError = false },
                getNavigate = {
                    val w = weight.toFloatOrNull()?.let { weightUnit.toKg(it) }
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
                        val w = weight.toFloatOrNull()?.let { weightUnit.toKg(it) } ?: 70f
                        waterIntake = "${WaterIntakeCalculator.calculateWaterIntake(h.toInt(), w.toInt(), activityLevel, gender ?: Gender.UNSPECIFIED)} ml"
                        currentStep = OnboardingStep.Loading
                    }
                },
                getBacK = { currentStep = OnboardingStep.BodyMeasurement }
            )
        }

        OnboardingStep.Loading -> {
            OnboardingLoadingScreen(
                modifier = backgroundModifier,
                getNavigate = { currentStep = OnboardingStep.WaterResult }
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
                    scope.launch {
                        val w = weight.toFloatOrNull()?.let { weightUnit.toKg(it) }?.toInt() ?: 70
                        val h = height.toFloatOrNull()?.toInt() ?: 170
                        val intake = waterIntake.replace(" ml", "").toIntOrNull() ?: 2000
                        val todayDate = currentTimeInfo().dateString

                        onboardingRepo.updateWaterAmount(
                            onUsedWater = 0,
                            onTotalWaterAmount = intake,
                            onWaterDay = todayDate
                        )
                        onboardingRepo.updateUserSettingsStore(
                            userHeight = h,
                            userWaterIntake = intake,
                            userName = "",
                            userWeight = w,
                            onBoardingCompleted = true
                        )
                        appPrefsRepo.setActivityLevel(activityLevel)
                        appPrefsRepo.setGender(gender ?: Gender.UNSPECIFIED)
                        appPrefsRepo.setWakeUpTime(wakeUpHour, 0)
                        appPrefsRepo.setBedTime(bedHour, 0)
                        currentStep = OnboardingStep.NotificationPermission
                    }
                },
                getBack = { currentStep = OnboardingStep.WaterResult }
            )
        }

        OnboardingStep.NotificationPermission -> {
            OnBoardingNotificationScreen(
                modifier = backgroundModifier,
                onFinished = { granted ->
                    scope.launch {
                        if (granted) appPrefsRepo.setNotificationsEnabled(true)
                        appPrefsRepo.setOnboardingCompleted(true)
                    }
                }
            )
        }
    }
}
