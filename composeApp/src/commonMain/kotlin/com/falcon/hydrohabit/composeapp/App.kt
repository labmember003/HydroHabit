package com.falcon.hydrohabit.composeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository

/**
 * Root composable for the Compose Multiplatform app.
 * This is a minimal proof-of-concept screen that reads from the shared module.
 */
@Composable
fun App(appPrefsRepo: AppPreferencesRepository) {
    val prefs by appPrefsRepo.cachedPreferences.collectAsState()

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HydroHabit (CMP)",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = if (prefs.onboardingCompleted) "Welcome back!" else "Onboarding not completed",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Notifications: ${if (prefs.notificationsEnabled) "ON" else "OFF"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

