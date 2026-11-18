package com.falcon.hydrohabit

import androidx.compose.ui.window.ComposeUIViewController
import com.falcon.hydrohabit.alarm.AlarmSchedulerContract
import com.falcon.hydrohabit.features.history.source.IntakeLogRepository
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import com.falcon.hydrohabit.tracking.HealthSync
import com.falcon.hydrohabit.tracking.HydrationTracker
import org.koin.mp.KoinPlatform
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    DroplyIos.start()
    val koin = KoinPlatform.getKoin()
    return ComposeUIViewController {
        App(
            appPrefsRepo = koin.get<AppPreferencesRepository>(),
            onboardingRepo = koin.get<OnboardingRepositoryContract>(),
            alarmScheduler = koin.get<AlarmSchedulerContract>(),
            tracker = koin.get<HydrationTracker>(),
            intakeLogRepo = koin.get<IntakeLogRepository>(),
            healthSync = koin.get<HealthSync>(),
        )
    }
}
