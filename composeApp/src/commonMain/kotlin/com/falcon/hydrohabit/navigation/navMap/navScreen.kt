package com.falcon.hydrohabit.navigation.navMap

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.falcon.hydrohabit.features.homescreen.HomeViewModel
import com.falcon.hydrohabit.features.onboarding.viewModel.OnboardingViewModel
import com.falcon.hydrohabit.navigation.navUtils.NavScreens
import com.falcon.hydrohabit.platform.KVStorage
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavScreen(
    onboardingViewModel: OnboardingViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    kvStorage: KVStorage = koinInject(),
    shouldOpenAddWater: Boolean = false,
    onAddWaterHandled: () -> Unit = {}
) {
    val navController = rememberNavController()
    val isOnboardingCompleted = kvStorage.getBoolean("onBoardingCompleted", false)
    var startDestination by remember {
        mutableStateOf(
            if (!isOnboardingCompleted) NavScreens.OnboardingNavHostingScreen.route
            else NavScreens.BottomNavHostingScreen.route
        )
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = NavScreens.BottomNavHostingScreen.route) {
            BottomBarHostingScreen(
                getUpdateTotalWaterTrackingAmount = {
                    onboardingViewModel.updateOnboardingWaterAmount(it)
                },
                onWaterTrackingResourceAmount = homeViewModel.usedWaterAmount,
                onTotalWaterTrackingResourceAmount = homeViewModel.totalWaterAmount,
                onReward = homeViewModel.rewardDialog,
                getWaterTrackingResourceAmount = { homeViewModel.fillWaterUpdate(it) },
                getReward = { if (it != null) homeViewModel.DismissReward(it) },
                onWaterMeterResourceAmount = homeViewModel.waterPercent,
                onProgress = homeViewModel.onProgress,
                onStreak = homeViewModel._streak.streak.toString(),
                onTime = homeViewModel.onTime,
                getGreeting = { homeViewModel.getGreeting() },
                items = mutableListOf(50, 100, 200, 300, 400, 500),
                streakImages = homeViewModel.perks,
                shouldOpenAddWater = shouldOpenAddWater,
                onAddWaterHandled = onAddWaterHandled,
            )
        }
        composable(route = NavScreens.OnboardingNavHostingScreen.route) {
            OnboardingNavHostingScreen(getNavigate = {
                homeViewModel.updateTotalWaterAmount(onboardingViewModel.onWaterAmount)
                navController.navigate(NavScreens.BottomNavHostingScreen.route) {
                    popUpTo(NavScreens.OnboardingNavHostingScreen.route) { inclusive = true }
                }
                onboardingViewModel.updateUserSettings(true)
            }, onboardingViewModel = onboardingViewModel)
        }
    }
}

