package com.falcon.hydrohabit.di

import android.content.Context
import android.content.SharedPreferences
import com.falcon.hydrohabit.features.calendarscreen.CalendarViewModel
import com.falcon.hydrohabit.features.homescreen.HomeViewModel
import com.falcon.hydrohabit.features.onboarding.viewModel.OnboardingViewModel
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepository
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val DIModule = module {
    // Provide Main Dispatcher
    single<CoroutineDispatcher>(named("mainDispatcher")) { Dispatchers.Main }
    // Provide IO Dispatcher
    single<CoroutineDispatcher>(named("ioDispatcher")) { Dispatchers.IO }
    // Provide Default Dispatcher
    single<CoroutineDispatcher>(named("defaultDispatcher")) { Dispatchers.Default }

    single<SharedPreferences>{
        androidContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    single<OnboardingRepositoryContract> {
        OnboardingRepository(get())
    }

    viewModel { OnboardingViewModel(get(),get()) }
    viewModel { HomeViewModel(get(), androidApplication()) }
    viewModel { CalendarViewModel(get()) }

}