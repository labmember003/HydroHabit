package com.falcon.hydrohabit.composeapp

import android.app.Application
import com.falcon.hydrohabit.shared.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ComposeAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ComposeAppApplication)
            modules(sharedModule(this@ComposeAppApplication))
        }
    }
}

