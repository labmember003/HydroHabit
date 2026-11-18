package com.falcon.hydrohabit

import com.falcon.hydrohabit.features.history.source.IntakeLogRepository
import com.falcon.hydrohabit.tracking.HealthSync
import com.falcon.hydrohabit.tracking.HydrationTracker
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.falcon.hydrohabit.alarm.AlarmSchedulerContract
import com.falcon.hydrohabit.alarm.ComposeNotificationService
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val appPrefsRepo: AppPreferencesRepository by inject()
    private val onboardingRepo: OnboardingRepositoryContract by inject()
    private val alarmScheduler: AlarmSchedulerContract by inject()
    private val tracker: HydrationTracker by inject()
    private val intakeLogRepo: IntakeLogRepository by inject()
    private val healthSync: HealthSync by inject()

    // Set when the notification's "Drink" action opens the app, so the water
    // picker dialog is shown on launch (matches app module behavior).
    private val shouldOpenAddWater = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // White status bar with dark icons — matches app module (Apple style)
        window.statusBarColor = Color.WHITE
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        handleIntent(intent)
        setContent {
            Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                App(
                    appPrefsRepo,
                    onboardingRepo,
                    alarmScheduler,
                    tracker,
                    intakeLogRepo,
                    healthSync,
                    shouldOpenAddWater = shouldOpenAddWater.value,
                    onAddWaterHandled = { shouldOpenAddWater.value = false }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("open_add_water", false) == true) {
            shouldOpenAddWater.value = true
            // Dismiss the reminder notification — setAutoCancel only fires on a
            // body tap, not on the "Drink" action button, so cancel it here.
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .cancel(ComposeNotificationService.NOTIFICATION_ID)
        }
    }
}

