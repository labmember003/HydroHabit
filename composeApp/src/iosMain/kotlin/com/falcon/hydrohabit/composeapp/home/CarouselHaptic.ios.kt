package com.falcon.hydrohabit.composeapp.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UISelectionFeedbackGenerator

@Composable
actual fun rememberCarouselHaptic(): () -> Unit {
    val generator = remember { UISelectionFeedbackGenerator() }
    return {
        generator.prepare()
        generator.selectionChanged()
    }
}
