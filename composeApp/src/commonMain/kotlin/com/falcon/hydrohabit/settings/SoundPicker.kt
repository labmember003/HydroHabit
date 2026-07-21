package com.falcon.hydrohabit.settings

import androidx.compose.runtime.Composable

val soundOptions = listOf(
    "Droplet",
    "Ripple",
    "Stream",
    "Cascade",
    "Splash",
    "System Default",
    "Custom",
)

data class CustomSoundPickerState(
    val launch: () -> Unit
)

@Composable
expect fun rememberCustomSoundPicker(
    existingUri: String?,
    onSoundPicked: (String) -> Unit
): CustomSoundPickerState

@Composable
expect fun SoundPickerDialog(
    currentIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
)

@Composable
expect fun resolveSoundDisplayName(soundIndex: Int, customSoundUri: String?): String
