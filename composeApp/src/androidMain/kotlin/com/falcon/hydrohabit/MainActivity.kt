package com.falcon.hydrohabit

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.falcon.hydrohabit.alarm.AlarmSchedulerContract
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val appPrefsRepo: AppPreferencesRepository by inject()
    private val onboardingRepo: OnboardingRepositoryContract by inject()
    private val alarmScheduler: AlarmSchedulerContract by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // White status bar with dark icons — matches app module (Apple style)
        window.statusBarColor = Color.WHITE
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        setContent {
            Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                App(appPrefsRepo, onboardingRepo, alarmScheduler)
            }
        }
    }
}

