package com.falcon.hydrohabit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = waterColor,
    onPrimary = Color.White,
    secondary = waterColor,
    tertiary = waterColor,
    background = Color(0xFF000000),
    onBackground = Color.White,
    surface = Color(0xFF1C1C1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFEBEBF5),
)

private val LightColorScheme = lightColorScheme(
    primary = waterColor,                         // iOS system blue
    onPrimary = Color.White,
    secondary = waterColor,
    onSecondary = Color.White,
    tertiary = primaryBlack,
    background = backgroundColor1,               // pure white
    onBackground = primaryBlack,                     // black text
    surface = backgroundColor1,                   // white surface
    onSurface = primaryBlack,
    surfaceVariant = onboardingBoxColor,           // light gray cards
    onSurfaceVariant = primaryBlack,
    surfaceTint = Color.Transparent,              // no tint
    outline = Color(0xFFD1D1D6),                  // iOS separator gray
    outlineVariant = Color(0xFFE5E5EA),
    error = textFieldErrorColor,
    onError = Color.White,
)

@Composable
fun HydroHabitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,               // disabled so our palette always wins
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // white status bar with dark icons — Apple style
            window.statusBarColor = Color.White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}