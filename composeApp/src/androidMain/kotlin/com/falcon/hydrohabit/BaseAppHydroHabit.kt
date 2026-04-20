package com.falcon.hydrohabit

import android.app.Application
import com.falcon.hydrohabit.di.androidModule
import com.falcon.hydrohabit.di.sharedModule
import com.falcon.hydrohabit.platform.JsonFileStorage
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseAppHydroHabit : Application() {
    override fun onCreate() {
        super.onCreate()
        JsonFileStorage.init(this)
        startKoin {
            androidLogger()
            androidContext(this@BaseAppHydroHabit)
            modules(sharedModule, androidModule)
        }
    }
}

