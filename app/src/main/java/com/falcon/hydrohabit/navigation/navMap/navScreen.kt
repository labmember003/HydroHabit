package com.falcon.hydrohabit.navigation.navMap

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.falcon.hydrohabit.features.homescreen.HomeViewModel
import com.falcon.hydrohabit.features.onboarding.viewModel.OnboardingViewModel
import com.falcon.hydrohabit.features.splash_screen.SplashScreen
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.waterColorBackground
import com.falcon.hydrohabit.navigation.navUtils.NavScreens
import com.falcon.hydrohabit.utils.Utils
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun NavScreen(
    OnboardingViewModel: OnboardingViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    sharedPreferences: SharedPreferences = koinInject()
) {
    val TAG = "NavScreen"
    val navController = rememberNavController()
    var startDestination by remember {
        mutableStateOf(NavScreens.SplashNavHostingScreen.route)
    }
    val isOnboardingCompleted = sharedPreferences.getBoolean("onBoardingCompleted", false)

    LaunchedEffect(Unit) {
        startDestination = if (!isOnboardingCompleted) {
            NavScreens.OnboardingNavHostingScreen.route
        } else {
            NavScreens.BottomNavHostingScreen.route
        }
        Utils.logIt(TAG, "onboarding Completed $startDestination")
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(route = NavScreens.BottomNavHostingScreen.route) {
            BottomBarHostingScreen(
                getUpdateTotalWaterTrackingAmount = {
                    OnboardingViewModel.updateOnboardingWaterAmount(it)
                    Log.d(
                        "totalWaterAmount OnboardingViewModel",
                        homeViewModel.totalWaterAmount.toString()
                    )
                },
                onUserName = OnboardingViewModel.onNameValue,
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
        composable(route = NavScreens.SplashNavHostingScreen.route) {
            SplashScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            start = Offset(Float.POSITIVE_INFINITY * 0.4f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY),
                            colors = mutableListOf(waterColorBackground, backgroundColor2)
                        )
                    )
            )
        }
    }
}
