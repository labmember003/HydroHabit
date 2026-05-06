package com.falcon.hydrohabit.navigation.navMap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.falcon.hydrohabit.features.homescreen.HomeViewModel
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.viewModel.OnboardingViewModel
import com.falcon.hydrohabit.navigation.navUtils.NavScreens
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun NavScreen(
    OnboardingViewModel: OnboardingViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    appPrefsRepo: AppPreferencesRepository = koinInject(),
    shouldOpenAddWater: Boolean = false,
    onAddWaterHandled: () -> Unit = {}
) {
    val TAG = "NavScreen"
    val navController = rememberNavController()

    // cachedPreferences is a StateFlow started EAGERLY at app launch (during DI init).
    // By the time this composable renders, the real disk value is already loaded — no flicker.
    val appPrefs by appPrefsRepo.cachedPreferences.collectAsState()
    val startDestination = if (appPrefs.onboardingCompleted) NavScreens.BottomNavHostingScreen.route
        else NavScreens.OnboardingNavHostingScreen.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(route = NavScreens.BottomNavHostingScreen.route) {
            BottomBarHostingScreen(
                getUpdateTotalWaterTrackingAmount = {
                    OnboardingViewModel.updateOnboardingWaterAmount(it)
                    println("totalWaterAmount OnboardingViewModel: ${homeViewModel.totalWaterAmount}")
                },
                onWaterTrackingResourceAmount = homeViewModel.usedWaterAmount,
                onTotalWaterTrackingResourceAmount = homeViewModel.totalWaterAmount,
                onReward = homeViewModel.rewardDialog,
                getWaterTrackingResourceAmount = {
                    homeViewModel.fillWaterUpdate(it)
                },
                getReward = {
                    if (it != null) {
                        homeViewModel.DismissReward(it)
                    }
                },
                onWaterMeterResourceAmount = homeViewModel.waterPercent,
                onProgress = homeViewModel.onProgress,
                onStreak = homeViewModel._streak.streak.toString(),
                onTime = homeViewModel.onTime,
                getGreeting = {
                    homeViewModel.getGreeting()
                },
                items = mutableListOf(50, 100, 200, 300, 400, 500),
                streakImages = homeViewModel.perks,
                shouldOpenAddWater = shouldOpenAddWater,
                onAddWaterHandled = onAddWaterHandled,
            )
        }
        composable(route = NavScreens.OnboardingNavHostingScreen.route) {
            OnboardingNavHostingScreen(getNavigate = {
                homeViewModel.updateTotalWaterAmount(OnboardingViewModel.onWaterAmount)
                navController.navigate(NavScreens.BottomNavHostingScreen.route) {
                    popUpTo(NavScreens.OnboardingNavHostingScreen.route) {
                        inclusive = true
                    }
                }
                OnboardingViewModel.updateUserSettings(true)
            }, onboardingViewModel = OnboardingViewModel)
        }
    }
}
