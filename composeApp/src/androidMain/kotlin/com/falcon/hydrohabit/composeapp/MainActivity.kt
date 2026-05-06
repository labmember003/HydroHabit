package com.falcon.hydrohabit.composeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val appPrefsRepo: AppPreferencesRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(appPrefsRepo)
        }
    }
}

