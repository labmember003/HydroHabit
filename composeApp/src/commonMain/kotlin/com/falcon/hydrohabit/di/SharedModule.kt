package com.falcon.hydrohabit.di

import com.falcon.hydrohabit.features.calendarscreen.CalendarViewModel
import com.falcon.hydrohabit.features.homescreen.HomeViewModel
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepository
import com.falcon.hydrohabit.features.onboarding.viewModel.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sharedModule = module {
    single<OnboardingRepository> { OnboardingRepository() }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { CalendarViewModel(get()) }
}

