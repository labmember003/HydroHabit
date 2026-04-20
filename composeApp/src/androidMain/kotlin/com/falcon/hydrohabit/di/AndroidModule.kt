package com.falcon.hydrohabit.di

import android.content.Context
import com.falcon.hydrohabit.platform.KVStorage
import com.falcon.hydrohabit.platform.NotificationScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<KVStorage> {
        KVStorage(androidContext().getSharedPreferences("prefs", Context.MODE_PRIVATE))
    }
    single<NotificationScheduler> {
        NotificationScheduler(androidContext())
    }
}

