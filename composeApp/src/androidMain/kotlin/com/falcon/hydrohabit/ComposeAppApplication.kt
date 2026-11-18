package com.falcon.hydrohabit

import com.falcon.hydrohabit.di.appModule
import com.falcon.hydrohabit.tracking.HealthSync
import com.falcon.hydrohabit.tracking.NoHealthSync
import android.app.Application
import com.falcon.hydrohabit.alarm.AlarmSchedulerContract
import com.falcon.hydrohabit.alarm.AndroidAlarmScheduler
import com.falcon.hydrohabit.shared.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ComposeAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ComposeAppApplication)
            modules(
                sharedModule(this@ComposeAppApplication),
                appModule(),
                module {
                    single<AlarmSchedulerContract> { AndroidAlarmScheduler(get()) }
                    single<HealthSync> { NoHealthSync }
                }
            )
        }
    }
}

