package com.falcon.hydrohabit.composeapp

import androidx.compose.ui.window.ComposeUIViewController
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.shared.di.sharedModule
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform
import platform.UIKit.UIViewController

/**
 * iOS entry point — called from Swift to get the root UIViewController.
 */
fun MainViewController(): UIViewController {
    initKoinIos()
    val appPrefsRepo = KoinPlatform.getKoin().get<AppPreferencesRepository>()
    return ComposeUIViewController {
        App(appPrefsRepo)
    }
}

private fun initKoinIos() {
    startKoin {
        modules(sharedModule(null))
    }
}


