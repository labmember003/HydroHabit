package com.falcon.hydrohabit

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
                module {
                    single<AlarmSchedulerContract> { AndroidAlarmScheduler(get()) }
                }
            )
        }
    }
}

