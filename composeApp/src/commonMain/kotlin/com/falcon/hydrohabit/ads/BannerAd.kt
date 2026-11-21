package com.falcon.hydrohabit.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * An anchored adaptive banner (Android only for now; nothing on iOS).
 * Takes no space until an ad has loaded, so there is never an empty strip.
 */
@Composable
expect fun BannerAd(modifier: Modifier = Modifier)

/** Opens the ad consent form again, or null when the user's region doesn't need it (UMP "privacy options"). */
@Composable
expect fun rememberAdPrivacyChoices(): (() -> Unit)?
