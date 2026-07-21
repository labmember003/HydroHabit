package com.falcon.hydrohabit

import androidx.compose.ui.window.ComposeUIViewController
import com.falcon.hydrohabit.alarm.AlarmSchedulerContract
import com.falcon.hydrohabit.alarm.IosAlarmScheduler
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import com.falcon.hydrohabit.shared.di.sharedModule
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoinIos()
    val koin = KoinPlatform.getKoin()
    val appPrefsRepo = koin.get<AppPreferencesRepository>()
    val onboardingRepo = koin.get<OnboardingRepositoryContract>()
    val alarmScheduler = koin.get<AlarmSchedulerContract>()
    return ComposeUIViewController {
        App(appPrefsRepo, onboardingRepo, alarmScheduler)
    }
}

private fun initKoinIos() {
    startKoin {
        modules(
            sharedModule(null),
            module {
                single<AlarmSchedulerContract> { IosAlarmScheduler() }
            }
        )
    }
}


