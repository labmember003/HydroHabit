package com.falcon.hydrohabit.features.profilescreen

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import com.falcon.hydrohabit.BuildConfig
import com.falcon.hydrohabit.features.profilescreen.utils.profileData
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyBold
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor
import com.falcon.hydrohabit.ui.theme.waterColorMeter

private val intervalOptions = buildList {
    if (BuildConfig.DEBUG) add("1 minute")
    addAll(listOf("30 minutes", "1 hour", "2 hours", "3 hours", "4 hours"))
}

val intervalMinutesMap = buildList {
    if (BuildConfig.DEBUG) add(1) // 1 minute for debug testing
    addAll(listOf(30, 60, 120, 180, 240))
}

val soundOptions = listOf(
    "Droplet",
    "Ripple",
    "Stream",
    "Cascade",
    "Splash",
    "System Default",
)

// Resource IDs for custom sounds (index 0-4), index 5 = system default (null)
fun getSoundResId(index: Int): Int? {
    return when (index) {
        0 -> com.falcon.hydrohabit.R.raw.water_drop_1
        1 -> com.falcon.hydrohabit.R.raw.water_drop_2
        2 -> com.falcon.hydrohabit.R.raw.water_drop_3
        3 -> com.falcon.hydrohabit.R.raw.water_drop_4
        4 -> com.falcon.hydrohabit.R.raw.water_drop_5
        else -> null // system default
    }
}

private fun formatTime(hour: Int, minute: Int = 0): String {
    val min = "%02d".format(minute)
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
    profileData: profileData,
    getNotificationChange: (Boolean) -> Unit,
    getIntervalChange: (Int) -> Unit,
    getWakeUpHourChange: (Int, Int) -> Unit,
    getBedHourChange: (Int, Int) -> Unit,
    getSoundChange: (Int) -> Unit,
) {
    val context = LocalContext.current
    var showIntervalDialog by remember { mutableStateOf(false) }
    var showWakeUpDialog by remember { mutableStateOf(false) }
    var showBedTimeDialog by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }

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

        // Notifications Section Header
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

        // Notification Intervals
        SettingsRow(
            title = "Reminder Interval",
            value = intervalOptions[profileData.selectedIntervalIndex],
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
            value = soundOptions[profileData.selectedSoundIndex],
            onClick = { showSoundDialog = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Reminders will be sent every ${intervalOptions[profileData.selectedIntervalIndex]} between ${formatTime(profileData.wakeUpHour, profileData.wakeUpMinute)} and ${formatTime(profileData.bedHour, profileData.bedMinute)}",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(400),
                color = Color(0xFF8E8E93),
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Support Section Header
        SectionHeader("Support")

        Spacer(modifier = Modifier.height(20.dp))

        // Report a Bug
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { sendBugReportEmail(context) }
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
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = "Report a Bug",
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF8E8E93)
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
            currentIndex = profileData.selectedIntervalIndex,
            onSelect = { index ->
                getIntervalChange(index)
                showIntervalDialog = false
            },
            onDismiss = { showIntervalDialog = false }
        )
    }

    // Wake Up Time Picker Dialog
    if (showWakeUpDialog) {
        TimePickerDialog(
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
        TimePickerDialog(
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
            context = context,
            currentIndex = profileData.selectedSoundIndex,
            onSelect = { index ->
                getSoundChange(index)
                showSoundDialog = false
            },
            onDismiss = { showSoundDialog = false }
        )
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
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = title,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF8E8E93)
            )
        }
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

@Composable
private fun SoundPickerDialog(
    context: Context,
    currentIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var previewIndex by remember { mutableStateOf(currentIndex) }

    // Clean up MediaPlayer when dialog closes
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun previewSound(index: Int) {
        mediaPlayer?.release()
        mediaPlayer = null
        val resId = getSoundResId(index)
        if (resId != null) {
            try {
                mediaPlayer = MediaPlayer.create(context, resId)
                mediaPlayer?.setOnCompletionListener { mp ->
                    mp.release()
                    if (mediaPlayer == mp) mediaPlayer = null
                }
                mediaPlayer?.start()
            } catch (_: Exception) { }
        } else {
            // System default — play default notification sound
            try {
                val defaultUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = android.media.RingtoneManager.getRingtone(context, defaultUri)
                ringtone?.play()
            } catch (_: Exception) { }
        }
    }

    Dialog(onDismissRequest = {
        mediaPlayer?.release()
        onDismiss()
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "Notification Sound",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamilyBold,
                    fontWeight = FontWeight(600),
                    color = primaryBlack,
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap to preview sound",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF8E8E93),
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            soundOptions.forEachIndexed { index, label ->
                val isSelected = index == previewIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            previewIndex = index
                            previewSound(index)
                        }
                        .background(
                            color = if (isSelected) waterColor.copy(alpha = 0.1f) else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (index < 5) "🔔 " else "📱 ",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = fontFamilyLight,
                                fontWeight = if (isSelected) FontWeight(500) else FontWeight(400),
                                color = if (isSelected) waterColor else primaryBlack,
                            )
                        )
                    }
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        mediaPlayer?.release()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFF8E8E93),
                        fontFamily = fontFamily,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        mediaPlayer?.release()
                        onSelect(previewIndex)
                    }
                ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
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

private fun sendBugReportEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("falcontechlab@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "HydroHabit - Bug Report")
        putExtra(Intent.EXTRA_TEXT, "Please describe the bug you encountered:\n\n")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        context.startActivity(Intent.createChooser(intent, "Send Bug Report"))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    start = Offset(Float.POSITIVE_INFINITY * 0.4f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                    colors = mutableListOf(waterColorMeter.copy(alpha = 0.1f), backgroundColor2)
                )
            ),
        profileData = profileData(
            onNotificationChange = false,
            selectedIntervalIndex = 1,
            wakeUpHour = 8,
            bedHour = 22,
            selectedSoundIndex = 0
        ),
        getNotificationChange = {},
        getIntervalChange = {},
        getWakeUpHourChange = { _, _ -> },
        getBedHourChange = { _, _ -> },
        getSoundChange = {}
    )
}