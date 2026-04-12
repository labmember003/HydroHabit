package com.falcon.hydrohabit.features.onboarding.presentation.sleepSchedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.falcon.hydrohabit.features.onboarding.utils.OnBoardingButtons
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyBold
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.primaryBlackLight
import com.falcon.hydrohabit.ui.theme.waterColor

private fun formatTime(hour: Int, minute: Int = 0): String {
    val min = "%02d".format(minute)
    return when {
        hour == 0 -> "12:$min AM"
        hour < 12 -> "$hour:$min AM"
        hour == 12 -> "12:$min PM"
        else -> "${hour - 12}:$min PM"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingSleepScheduleScreen(
    modifier: Modifier = Modifier,
    selectedWakeUpHour: Int,
    selectedBedHour: Int,
    getWakeUpChange: (Int) -> Unit,
    getBedTimeChange: (Int) -> Unit,
    getNavigate: () -> Unit,
    getBack: () -> Unit,
) {
    var wakeHour by remember { mutableIntStateOf(selectedWakeUpHour) }
    var wakeMinute by remember { mutableIntStateOf(0) }
    var bedHour by remember { mutableIntStateOf(selectedBedHour) }
    var bedMinute by remember { mutableIntStateOf(0) }

    var showWakePicker by remember { mutableStateOf(false) }
    var showBedPicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Daily Rhythm",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = fontFamilyBold,
                        fontWeight = FontWeight(600),
                        color = primaryBlack,
                        textAlign = TextAlign.Center,
                    ), modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Tell us your schedule so we only remind you\nto drink water when you're awake",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = fontFamilyLight,
                        fontWeight = FontWeight(300),
                        color = primaryBlackLight,
                        textAlign = TextAlign.Center,
                    ), modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Wake Up Time
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "☀️  Wake Up Time",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(500),
                        color = primaryBlackLight,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFD1D1D6), RoundedCornerShape(12.dp))
                        .clickable { showWakePicker = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = formatTime(wakeHour, wakeMinute),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight(400),
                            color = primaryBlack,
                        )
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Bed Time
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "🌙  Bed Time",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(500),
                        color = primaryBlackLight,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFD1D1D6), RoundedCornerShape(12.dp))
                        .clickable { showBedPicker = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = formatTime(bedHour, bedMinute),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight(400),
                            color = primaryBlack,
                        )
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBoardingButtons(
                    getNavigate = {
                        getWakeUpChange(wakeHour)
                        getBedTimeChange(bedHour)
                        getNavigate()
                    },
                    getBack = getBack
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Wake Up Time Picker Dialog
    if (showWakePicker) {
        LightTimePickerDialog(
            title = "Wake Up Time",
            initialHour = wakeHour,
            initialMinute = wakeMinute,
            onConfirm = { h, m ->
                wakeHour = h
                wakeMinute = m
                showWakePicker = false
            },
            onDismiss = { showWakePicker = false }
        )
    }

    // Bed Time Picker Dialog
    if (showBedPicker) {
        LightTimePickerDialog(
            title = "Bed Time",
            initialHour = bedHour,
            initialMinute = bedMinute,
            onConfirm = { h, m ->
                bedHour = h
                bedMinute = m
                showBedPicker = false
            },
            onDismiss = { showBedPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LightTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = fontFamilyBold,
                    fontWeight = FontWeight(600),
                    color = primaryBlack,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            TimePicker(
                state = state,
                colors = TimePickerDefaults.colors(
                    clockDialColor = Color(0xFFF2F2F7),
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = primaryBlack,
                    selectorColor = waterColor,
                    containerColor = Color.White,
                    periodSelectorBorderColor = Color(0xFFD1D1D6),
                    periodSelectorSelectedContainerColor = waterColor.copy(alpha = 0.15f),
                    periodSelectorUnselectedContainerColor = Color.White,
                    periodSelectorSelectedContentColor = waterColor,
                    periodSelectorUnselectedContentColor = Color(0xFF8E8E93),
                    timeSelectorSelectedContainerColor = waterColor.copy(alpha = 0.15f),
                    timeSelectorUnselectedContainerColor = Color(0xFFF2F2F7),
                    timeSelectorSelectedContentColor = waterColor,
                    timeSelectorUnselectedContentColor = primaryBlack,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color(0xFF8E8E93), fontFamily = fontFamily, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { onConfirm(state.hour, state.minute) }) {
                    Text("Done", color = waterColor, fontFamily = fontFamilyBold, fontSize = 14.sp)
                }
            }
        }
    }
}
