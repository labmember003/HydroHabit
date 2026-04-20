package com.falcon.hydrohabit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.falcon.hydrohabit.navigation.navMap.NavScreen
import com.falcon.hydrohabit.ui.theme.HydroHabitTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {

    val shouldOpenAddWater = mutableStateOf(false)

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            HydroHabitTheme {
                KoinAndroidContext {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding(),
                        color = Color.Transparent
                    ) {
                        NavScreen(shouldOpenAddWater = shouldOpenAddWater.value, onAddWaterHandled = { shouldOpenAddWater.value = false })
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("open_add_water", false) == true) {
            shouldOpenAddWater.value = true
        }
    }
}
