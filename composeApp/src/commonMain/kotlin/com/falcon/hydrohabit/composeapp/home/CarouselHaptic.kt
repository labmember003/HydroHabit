package com.falcon.hydrohabit.composeapp.home

import androidx.compose.runtime.Composable

/**
 * Returns a callback that fires a short "tick" haptic, used as the carousel picker
 * moves between items. Android uses CLOCK_TICK (matching the production app module);
 * iOS uses a selection feedback tick.
 */
@Composable
expect fun rememberCarouselHaptic(): () -> Unit
