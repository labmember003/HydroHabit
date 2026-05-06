package com.falcon.hydrohabit.shared.di

import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import com.falcon.hydrohabit.features.onboarding.source.SharedOnboardingRepository
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Shared Koin module providing all KMM-compatible dependencies.
 *
 * @param context platform context — Android Context on Android, null on iOS.
 *
 * Usage:
 *   Android: startKoin { modules(sharedModule(applicationContext), androidModule) }
 *   iOS:     startKoin { modules(sharedModule(null)) }
 */
fun sharedModule(context: Any?): Module = module {

    single<OnboardingRepositoryContract> {
        SharedOnboardingRepository(context = context)
    }

    single<AppPreferencesRepository> {
        AppPreferencesRepository(context = context)
    }
}

