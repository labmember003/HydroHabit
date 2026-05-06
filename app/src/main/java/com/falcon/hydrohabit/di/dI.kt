package com.falcon.hydrohabit.di

import android.content.Context
import android.content.SharedPreferences
import com.falcon.hydrohabit.features.calendarscreen.CalendarViewModel
import com.falcon.hydrohabit.features.homescreen.HomeViewModel
import com.falcon.hydrohabit.features.onboarding.viewModel.OnboardingViewModel
import com.falcon.hydrohabit.shared.AndroidNotificationPermissionHandler
import com.falcon.hydrohabit.shared.NotificationPermissionHandler
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Android-specific module — ViewModels, permissions, SharedPreferences, dispatchers.
 */
val androidDIModule = module {
    // Dispatchers
    single<CoroutineDispatcher>(named("mainDispatcher")) { Dispatchers.Main }
    single<CoroutineDispatcher>(named("ioDispatcher")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("defaultDispatcher")) { Dispatchers.Default }

    // Legacy SharedPreferences — kept for notification service (reads sound settings synchronously)
    single<SharedPreferences> {
        androidContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    // moko-permissions controller
    single<PermissionsController> {
        PermissionsController(applicationContext = androidApplication())
    }

    single<NotificationPermissionHandler> {
        AndroidNotificationPermissionHandler(get())
    }

    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), androidApplication()) }
    viewModel { CalendarViewModel(get()) }

}