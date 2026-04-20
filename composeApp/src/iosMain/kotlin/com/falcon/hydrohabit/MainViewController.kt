package com.falcon.hydrohabit

import androidx.compose.ui.window.ComposeUIViewController
import com.falcon.hydrohabit.di.iosModule
import com.falcon.hydrohabit.di.sharedModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    App()
}

fun initKoin() {
    startKoin {
        modules(sharedModule, iosModule)
    }
}

