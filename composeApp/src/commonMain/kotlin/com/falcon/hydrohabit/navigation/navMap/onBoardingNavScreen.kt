package com.falcon.hydrohabit.navigation.navMap

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.falcon.hydrohabit.features.onboarding.presentation.activityMeasurement.OnBoardingActiveScreen
import com.falcon.hydrohabit.features.onboarding.presentation.bodyMeasurement.OnBoardingBodyMeasurementsScreen
import com.falcon.hydrohabit.features.onboarding.presentation.permissionScreens.OnboardingNotifications
import com.falcon.hydrohabit.features.onboarding.presentation.resultMeasurement.OnBoardingWaterIntakeResultScreen
import com.falcon.hydrohabit.features.onboarding.presentation.sleepSchedule.OnBoardingSleepScheduleScreen
import com.falcon.hydrohabit.features.onboarding.utils.BodyMeasurementData
import com.falcon.hydrohabit.features.onboarding.utils.OnboardingLoadingScreen
import com.falcon.hydrohabit.features.onboarding.utils.activityMeasurementData
import com.falcon.hydrohabit.features.onboarding.viewModel.OnboardingViewModel
import com.falcon.hydrohabit.navigation.navUtils.OnboardingNavScreens
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.waterColorBackground
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingNavHostingScreen(
    navController: NavHostController = rememberNavController(),
    getNavigate: () -> Unit,
    onboardingViewModel: OnboardingViewModel = koinViewModel()
) {
    val bgModifier = Modifier.fillMaxSize().background(
        Brush.linearGradient(
            start = Offset(Float.POSITIVE_INFINITY * 0.4f, 0f),
            end = Offset(0f, Float.POSITIVE_INFINITY),
            colors = listOf(waterColorBackground, backgroundColor2)
        )
    )

    NavHost(
        navController = navController,
        startDestination = OnboardingNavScreens.BodyMeasurementScreen.route,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) }
    ) {
        composable(route = OnboardingNavScreens.LoadingScreen.route) {
            OnboardingLoadingScreen(modifier = bgModifier, getNavigate = {
                navController.navigate(OnboardingNavScreens.WaterIntakeResultScreen.route) {
                    popUpTo(OnboardingNavScreens.LoadingScreen.route) { inclusive = true }
                }
            })
        }
        composable(route = OnboardingNavScreens.ActivityIntakeScreen.route) {
            OnBoardingActiveScreen(
                modifier = bgModifier,
                getActiveOutcome = { onboardingViewModel.onBoardingActiveScreen(it) },
                activityMeasurementData = activityMeasurementData(
                    onErrorText = onboardingViewModel.onActivityLevelError,
                    onActivityOutcome = onboardingViewModel.onActiveValue ?: -1,
                    checkError = onboardingViewModel.onActivityLevelCheck
                ),
                getNavigate = {
                    onboardingViewModel.checkActivtyLevels()
                    if (!onboardingViewModel.onActivityLevelCheck && onboardingViewModel.onActiveValue != Int.MIN_VALUE) {
                        onboardingViewModel.calculateWaterIntake()
                        navController.navigate(OnboardingNavScreens.LoadingScreen.route)
                    }
                },
                getBacK = {
                    navController.navigateUp()
                    navController.popBackStack(OnboardingNavScreens.ActivityIntakeScreen.route, inclusive = true)
                }
            )
        }
        composable(route = OnboardingNavScreens.WaterIntakeResultScreen.route) {
            OnBoardingWaterIntakeResultScreen(
                modifier = bgModifier,
                getNavigate = {
                    onboardingViewModel.updateUserSettings(false)
                    navController.navigate(OnboardingNavScreens.SleepScheduleScreen.route)
                },
                onWaterIntake = "${onboardingViewModel.onWaterAmount} ml",
                getBack = {
                    navController.navigateUp()
                    navController.popBackStack(OnboardingNavScreens.ActivityIntakeScreen.route, false)
                }
            )
        }
        composable(route = OnboardingNavScreens.SleepScheduleScreen.route) {
            OnBoardingSleepScheduleScreen(
                modifier = bgModifier,
                selectedWakeUpHour = onboardingViewModel.wakeUpHour,
                selectedBedHour = onboardingViewModel.bedHour,
                getWakeUpChange = { onboardingViewModel.updateWakeUpHour(it) },
                getBedTimeChange = { onboardingViewModel.updateBedHour(it) },
                getNavigate = {
                    onboardingViewModel.saveSleepSchedule()
                    navController.navigate(OnboardingNavScreens.NotificationPermissionScreen.route)
                },
                getBack = { navController.navigateUp() }
            )
        }
        composable(route = OnboardingNavScreens.BodyMeasurementScreen.route) {
            OnBoardingBodyMeasurementsScreen(
                getWeightChange = { onboardingViewModel.onWeightChange(it) },
                getHeightChange = { onboardingViewModel.onHeightChange(it) },
                getNavigate = {
                    onboardingViewModel.checkBodyMeasurementsFields()
                    if (!onboardingViewModel.onWeightCheck && !onboardingViewModel.onHeightCheck) {
                        navController.navigate(OnboardingNavScreens.ActivityIntakeScreen.route)
                    }
                },
                modifier = bgModifier,
                bodyMeasurementData = BodyMeasurementData(
                    onWeightChange = onboardingViewModel.onWeightValue,
                    onHeightChange = onboardingViewModel.onHeightValue,
                    onWeightCheck = onboardingViewModel.onWeightCheck,
                    onHeightCheck = onboardingViewModel.onHeightCheck,
                    onHeightError = onboardingViewModel.onHeightError,
                    onWeightError = onboardingViewModel.onWeightError
                )
            )
        }
        composable(route = OnboardingNavScreens.NotificationPermissionScreen.route) {
            OnboardingNotifications(
                modifier = bgModifier,
                getAllow = { getNavigate() },
                onPermissionDenied = onboardingViewModel.onNotificationPermissionDenied ?: false,
                getPermissionDenied = { onboardingViewModel.updatePermissionNotification(it) }
            )
        }
    }
}

