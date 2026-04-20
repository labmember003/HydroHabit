package com.falcon.hydrohabit.di

import com.falcon.hydrohabit.platform.KVStorage
import com.falcon.hydrohabit.platform.NotificationScheduler
import org.koin.dsl.module

val iosModule = module {
    single<KVStorage> { KVStorage() }
    single<NotificationScheduler> { NotificationScheduler() }
}

