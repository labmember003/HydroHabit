package com.falcon.hydrohabit.di

import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.tracking.HydrationTracker
import org.koin.dsl.module

/** App-level singletons shared by Android and iOS (platforms add their own HealthSync). */
fun appModule() = module {
    single {
        val prefs = get<AppPreferencesRepository>()
        HydrationTracker(
            onboardingRepo = get(),
            intakeLog = get(),
            healthSync = get(),
            isHealthSyncEnabled = { prefs.current().healthSyncEnabled },
        )
    }
}
