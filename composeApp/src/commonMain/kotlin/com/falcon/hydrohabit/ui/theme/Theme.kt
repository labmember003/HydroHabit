package com.falcon.hydrohabit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
    primary = waterColor,
    onPrimary = Color.White,
    secondary = waterColor,
    onSecondary = Color.White,
    tertiary = primaryBlack,
    background = backgroundColor1,
    onBackground = primaryBlack,
    surface = backgroundColor1,
    onSurface = primaryBlack,
    surfaceVariant = onboardingBoxColor,
    onSurfaceVariant = primaryBlack,
    surfaceTint = Color.Transparent,
    outline = Color(0xFFD1D1D6),
    outlineVariant = Color(0xFFE5E5EA),
    error = textFieldErrorColor,
    onError = Color.White,
)

@Composable
fun HydroHabitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

