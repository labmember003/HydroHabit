package com.falcon.hydrohabit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.falcon.hydrohabit.navigation.navMap.NavScreen
import com.falcon.hydrohabit.ui.theme.HydroHabitTheme

@Composable
fun App() {
    HydroHabitTheme {
        Surface(
            modifier = Modifier.fillMaxSize().safeDrawingPadding(),
            color = Color.Transparent
        ) {
            NavScreen()
        }
    }
}

