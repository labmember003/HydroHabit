package com.falcon.hydrohabit.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.falcon.hydrohabit.onboarding.components.WeightUnit
import com.falcon.hydrohabit.onboarding.components.WeightUnitSelector
import com.falcon.hydrohabit.onboarding.components.fromKg
import com.falcon.hydrohabit.onboarding.components.toKg
import kotlin.math.roundToInt
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyBold
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.primaryBlackLight
import com.falcon.hydrohabit.ui.theme.waterColor
import com.falcon.hydrohabit.features.profilescreen.utils.ProfileData

val intervalOptions = buildList {
    if (isDebugBuild) add("1 minute")
    addAll(listOf("30 minutes", "1 hour", "2 hours", "3 hours", "4 hours"))
}

val intervalMinutesMap = buildList {
    if (isDebugBuild) add(1) // 1 minute for debug testing
    addAll(listOf(30, 60, 120, 180, 240))
}

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
fun SettingsScreen(
    modifier: Modifier = Modifier,
    profileData: ProfileData,
    heightCm: Int,
    weightKg: Int,
    initialWeightUnit: WeightUnit = WeightUnit.KG,
    getWeightUnitChange: (WeightUnit) -> Unit = {},
    getNotificationChange: (Boolean) -> Unit,
    getIntervalChange: (Int) -> Unit,
    getWakeUpHourChange: (Int, Int) -> Unit,
    getBedHourChange: (Int, Int) -> Unit,
    getSoundChange: (Int) -> Unit,
    onCustomSoundPicked: (String) -> Unit,
    getHeightChange: (Int) -> Unit,
    getWeightChange: (Int) -> Unit,
) {
    var showIntervalDialog by remember { mutableStateOf(false) }
    var showWakeUpDialog by remember { mutableStateOf(false) }
    var showBedTimeDialog by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }
    var showHeightDialog by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }

    val customSoundPicker = rememberCustomSoundPicker(
        existingUri = profileData.customSoundUri,
        onSoundPicked = { uri ->
            onCustomSoundPicked(uri)
            getSoundChange(6)
        }
    )

    val soundDisplayName = resolveSoundDisplayName(
        soundIndex = profileData.selectedSoundIndex,
        customSoundUri = profileData.customSoundUri
    )

    val platformActions = rememberPlatformActions()
    val exactAlarmState = rememberExactAlarmState()

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Settings",
            style = TextStyle(
                fontSize = 28.sp,
                fontFamily = fontFamilyBold,
                fontWeight = FontWeight(600),
                color = primaryBlack,
                textAlign = TextAlign.Start,
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Section
        SectionHeader("Profile")

        Spacer(modifier = Modifier.height(20.dp))

        // Height
        SettingsRow(
            title = "Height",
            value = if (heightCm > 0) "$heightCm cm" else "Not set",
            onClick = { showHeightDialog = true }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Weight
        SettingsRow(
            title = "Weight",
            value = if (weightKg > 0) {
                val display = initialWeightUnit.fromKg(weightKg.toFloat()).roundToInt()
                "$display ${initialWeightUnit.label.lowercase()}"
            } else "Not set",
            onClick = { showWeightDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader("Notifications")

        Spacer(modifier = Modifier.height(20.dp))

        // Notifications Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Notifications",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                )
            )
            Switch(
                checked = profileData.onNotificationChange,
                onCheckedChange = getNotificationChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = waterColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE9E9EB),
                    uncheckedBorderColor = Color.Transparent,
                ),
                thumbContent = if (profileData.onNotificationChange) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Notification",
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Reminder Interval
        SettingsRow(
            title = "Reminder Interval",
            value = intervalOptions[profileData.selectedIntervalIndex.coerceIn(intervalOptions.indices)],
            onClick = { showIntervalDialog = true }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Wake Up Time
        SettingsRow(
            title = "Wake Up Time",
            value = formatTime(profileData.wakeUpHour, profileData.wakeUpMinute),
            onClick = { showWakeUpDialog = true }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Bed Time
        SettingsRow(
            title = "Bed Time",
            value = formatTime(profileData.bedHour, profileData.bedMinute),
            onClick = { showBedTimeDialog = true }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Notification Sound
        SettingsRow(
            title = "Notification Sound",
            value = soundDisplayName,
            onClick = { showSoundDialog = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        val safeIndex = profileData.selectedIntervalIndex.coerceIn(intervalOptions.indices)
        Text(
            text = "Reminders will be sent every ${intervalOptions[safeIndex]} between ${formatTime(profileData.wakeUpHour, profileData.wakeUpMinute)} and ${formatTime(profileData.bedHour, profileData.bedMinute)}",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(400),
                color = Color(0xFF8E8E93),
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Shown only when reminders are on AND the OS is throttling exact timing
        // (Android 12+ with "Alarms & reminders" access not granted). Tapping opens
        // the system settings page; the warning hides itself once access is granted.
        if (profileData.onNotificationChange && exactAlarmState.showPrecisionWarning) {
            Spacer(modifier = Modifier.height(12.dp))
            ExactAlarmWarningCard(onClick = exactAlarmState.openExactAlarmSettings)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Support Section
        SectionHeader("Support")

        Spacer(modifier = Modifier.height(20.dp))

        // Report a Bug
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { platformActions.sendBugReport() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Report a Bug",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                )
            )
            Text(
                text = ">",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF8E8E93),
                )
            )
        }

        Spacer(modifier = Modifier.height(25.dp))
    }

    // Interval Picker Dialog
    if (showIntervalDialog) {
        OptionPickerDialog(
            title = "Reminder Interval",
            subtitle = "How often should we remind you to drink water?",
            options = intervalOptions.map { "Every $it" },
            currentIndex = profileData.selectedIntervalIndex.coerceIn(intervalOptions.indices),
            onSelect = { index ->
                getIntervalChange(index)
                showIntervalDialog = false
            },
            onDismiss = { showIntervalDialog = false }
        )
    }

    // Wake Up Time Picker Dialog
    if (showWakeUpDialog) {
        SimpleTimePickerDialog(
            title = "Wake Up Time",
            initialHour = profileData.wakeUpHour,
            initialMinute = profileData.wakeUpMinute,
            onConfirm = { hour, minute ->
                getWakeUpHourChange(hour, minute)
                showWakeUpDialog = false
            },
            onDismiss = { showWakeUpDialog = false }
        )
    }

    // Bed Time Picker Dialog
    if (showBedTimeDialog) {
        SimpleTimePickerDialog(
            title = "Bed Time",
            initialHour = profileData.bedHour,
            initialMinute = profileData.bedMinute,
            onConfirm = { hour, minute ->
                getBedHourChange(hour, minute)
                showBedTimeDialog = false
            },
            onDismiss = { showBedTimeDialog = false }
        )
    }

    // Sound Picker Dialog
    if (showSoundDialog) {
        SoundPickerDialog(
            currentIndex = profileData.selectedSoundIndex,
            onSelect = { index ->
                if (index == 6) {
                    showSoundDialog = false
                    customSoundPicker.launch()
                } else {
                    getSoundChange(index)
                    showSoundDialog = false
                }
            },
            onDismiss = { showSoundDialog = false }
        )
    }

    // Height Input Dialog
    if (showHeightDialog) {
        NumberInputDialog(
            title = "Height",
            unit = "cm",
            initialValue = heightCm,
            onConfirm = { value ->
                getHeightChange(value)
                showHeightDialog = false
            },
            onDismiss = { showHeightDialog = false }
        )
    }

    // Weight Input Dialog
    if (showWeightDialog) {
        NumberInputDialog(
            title = "Weight",
            unit = "kg",
            initialValue = weightKg,
            weightUnitToggle = true,
            initialUnit = initialWeightUnit,
            onUnitChange = getWeightUnitChange,
            onConfirm = { value ->
                getWeightChange(value)
                showWeightDialog = false
            },
            onDismiss = { showWeightDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberInputDialog(
    title: String,
    unit: String,
    initialValue: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
    weightUnitToggle: Boolean = false,
    initialUnit: WeightUnit = WeightUnit.KG,
    onUnitChange: (WeightUnit) -> Unit = {}
) {
    var selectedUnit by remember { mutableStateOf(initialUnit) }
    var text by remember {
        mutableStateOf(
            if (initialValue > 0) {
                initialUnit.fromKg(initialValue.toFloat()).roundToInt().toString()
            } else ""
        )
    }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamilyBold,
                    fontWeight = FontWeight(600),
                    color = primaryBlack,
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { new ->
                    text = new.filter { it.isDigit() }.take(3)
                    isError = false
                },
                singleLine = true,
                isError = isError,
                suffix = {
                    if (weightUnitToggle) {
                        WeightUnitSelector(
                            selected = selectedUnit,
                            onSelected = { newUnit ->
                                if (newUnit != selectedUnit) {
                                    text = text.toFloatOrNull()
                                        ?.let { selectedUnit.toKg(it) }
                                        ?.let { newUnit.fromKg(it).roundToInt().toString() }
                                        ?: text
                                    selectedUnit = newUnit
                                    isError = false
                                    onUnitChange(newUnit)
                                }
                            }
                        )
                    } else {
                        Text(unit, color = Color(0xFF8E8E93), fontFamily = fontFamily)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = fontFamily,
                    color = primaryBlack,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = waterColor,
                    unfocusedBorderColor = Color(0xFFD1D1D6),
                    cursorColor = waterColor,
                    errorBorderColor = Color(0xFFFF3B30),
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (isError) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Please enter a valid ${title.lowercase()}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = fontFamily,
                        color = Color(0xFFFF3B30),
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFF8E8E93),
                        fontFamily = fontFamily,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    val value = text.toFloatOrNull()
                        ?.let { if (weightUnitToggle) selectedUnit.toKg(it) else it }
                        ?.roundToInt()
                    if (value == null || value <= 0 || value > 300) {
                        isError = true
                    } else {
                        onConfirm(value)
                    }
                }) {
                    Text(
                        text = "Done",
                        color = waterColor,
                        fontFamily = fontFamilyBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(400),
                color = primaryBlack,
            )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF8E8E93),
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = ">",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF8E8E93),
                )
            )
        }
    }
}

@Composable
private fun ExactAlarmWarningCard(onClick: () -> Unit) {
    val accent = Color(0xFFFF9F0A)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = accent.copy(alpha = 0.10f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Reminders may be delayed",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = fontFamilyBold,
                    fontWeight = FontWeight(600),
                    color = primaryBlack,
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Allow precise timing so reminders arrive exactly on schedule.",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF8E8E93),
                )
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Fix",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = fontFamilyBold,
                fontWeight = FontWeight(600),
                color = waterColor,
            )
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = fontFamilyBold,
                fontWeight = FontWeight(400),
                color = primaryBlack,
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Color(0xFFD1D1D6))
        )
    }
}

@Composable
private fun OptionPickerDialog(
    title: String,
    subtitle: String,
    options: List<String>,
    currentIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamilyBold,
                    fontWeight = FontWeight(600),
                    color = primaryBlack,
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF8E8E93),
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            options.forEachIndexed { index, label ->
                val isSelected = index == currentIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(index) }
                        .background(
                            color = if (isSelected) waterColor.copy(alpha = 0.1f) else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = fontFamilyLight,
                            fontWeight = if (isSelected) FontWeight(500) else FontWeight(400),
                            color = if (isSelected) waterColor else primaryBlack,
                        )
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = waterColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0xFF8E8E93),
                    fontFamily = fontFamily,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
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
                    fontSize = 20.sp,
                    fontFamily = fontFamilyBold,
                    fontWeight = FontWeight(600),
                    color = primaryBlack,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            TimePicker(
                state = timePickerState,
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
                    Text(
                        text = "Cancel",
                        color = Color(0xFF8E8E93),
                        fontFamily = fontFamily,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                }) {
                    Text(
                        text = "Done",
                        color = waterColor,
                        fontFamily = fontFamilyBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
