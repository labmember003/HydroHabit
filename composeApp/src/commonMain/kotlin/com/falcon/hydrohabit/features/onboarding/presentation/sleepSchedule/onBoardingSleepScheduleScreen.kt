package com.falcon.hydrohabit.features.onboarding.presentation.sleepSchedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
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
import com.falcon.hydrohabit.ui.theme.*

private fun formatTime(hour: Int, minute: Int = 0): String {
    val min = minute.toString().padStart(2, '0')
    return when {
        hour == 0 -> "12:$min AM"
        hour < 12 -> "$hour:$min AM"
        hour == 12 -> "12:$min PM"
        else -> "${hour - 12}:$min PM"
    }
}

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
    var bedHour by remember { mutableIntStateOf(selectedBedHour) }
    var showWakePicker by remember { mutableStateOf(false) }
    var showBedPicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Your Daily Rhythm", style = TextStyle(fontSize = 20.sp, fontFamily = fontFamilyBold, fontWeight = FontWeight(600), color = primaryBlack, textAlign = TextAlign.Center), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Tell us your schedule so we only remind you\nto drink water when you're awake", style = TextStyle(fontSize = 14.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(300), color = primaryBlackLight, textAlign = TextAlign.Center), modifier = Modifier.fillMaxWidth())
            }
        }
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Text(text = "☀️  Wake Up Time", style = TextStyle(fontSize = 14.sp, fontFamily = fontFamily, fontWeight = FontWeight(500), color = primaryBlackLight))
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(56.dp).background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFD1D1D6), RoundedCornerShape(12.dp)).clickable { showWakePicker = true }.padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = formatTime(wakeHour), style = TextStyle(fontSize = 18.sp, fontFamily = fontFamily, fontWeight = FontWeight(400), color = primaryBlack))
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Text(text = "🌙  Bed Time", style = TextStyle(fontSize = 14.sp, fontFamily = fontFamily, fontWeight = FontWeight(500), color = primaryBlackLight))
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(56.dp).background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFD1D1D6), RoundedCornerShape(12.dp)).clickable { showBedPicker = true }.padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = formatTime(bedHour), style = TextStyle(fontSize = 18.sp, fontFamily = fontFamily, fontWeight = FontWeight(400), color = primaryBlack))
                }
            }
        }
        item { Spacer(modifier = Modifier.height(40.dp)) }
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                OnBoardingButtons(
                    getNavigate = { getWakeUpChange(wakeHour); getBedTimeChange(bedHour); getNavigate() },
                    getBack = getBack
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showWakePicker) {
        SimpleHourPickerDialog(title = "Wake Up Time", initialHour = wakeHour, onConfirm = { wakeHour = it; showWakePicker = false }, onDismiss = { showWakePicker = false })
    }
    if (showBedPicker) {
        SimpleHourPickerDialog(title = "Bed Time", initialHour = bedHour, onConfirm = { bedHour = it; showBedPicker = false }, onDismiss = { showBedPicker = false })
    }
}

@Composable
private fun SimpleHourPickerDialog(title: String, initialHour: Int, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(16.dp)).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = TextStyle(fontSize = 18.sp, fontFamily = fontFamilyBold, fontWeight = FontWeight(600), color = primaryBlack), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(20.dp))

            // Simple hour grid
            val hours = (0..23).toList()
            Column {
                for (row in hours.chunked(6)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        row.forEach { hour ->
                            val isSelected = hour == selectedHour
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(if (isSelected) waterColor else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { selectedHour = hour },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = formatTime(hour),
                                    style = TextStyle(fontSize = 11.sp, color = if (isSelected) Color.White else primaryBlack, fontFamily = fontFamily),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF8E8E93), fontFamily = fontFamily, fontSize = 14.sp) }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { onConfirm(selectedHour) }) { Text("Done", color = waterColor, fontFamily = fontFamilyBold, fontSize = 14.sp) }
            }
        }
    }
}

