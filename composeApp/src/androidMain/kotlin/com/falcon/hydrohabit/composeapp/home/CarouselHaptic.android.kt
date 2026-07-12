package com.falcon.hydrohabit.composeapp.home

import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberCarouselHaptic(): () -> Unit {
    val view = LocalView.current
    return { view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK) }
}