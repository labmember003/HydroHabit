package com.falcon.hydrohabit.features.profilescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
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
import com.falcon.hydrohabit.features.profilescreen.utils.ProfileData
import com.falcon.hydrohabit.platform.isDebugBuild
import com.falcon.hydrohabit.platform.sendBugReportEmail
import com.falcon.hydrohabit.ui.theme.*

val intervalOptions = buildList {
    if (isDebugBuild()) add("1 minute")
    addAll(listOf("30 minutes", "1 hour", "2 hours", "3 hours", "4 hours"))
}

val intervalMinutesMap = buildList {
    if (isDebugBuild()) add(1)
    addAll(listOf(30, 60, 120, 180, 240))
}

val soundOptions = listOf(
    "Droplet", "Ripple", "Stream", "Cascade", "Splash", "System Default", "Custom",
)

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
    getNotificationChange: (Boolean) -> Unit,
    getIntervalChange: (Int) -> Unit,
    getWakeUpHourChange: (Int, Int) -> Unit,
    getBedHourChange: (Int, Int) -> Unit,
    getSoundChange: (Int) -> Unit,
    onCustomSoundPicked: (String) -> Unit,
) {
    var showIntervalDialog by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Settings",
            style = TextStyle(fontSize = 28.sp, fontFamily = fontFamilyBold, fontWeight = FontWeight(600), color = primaryBlack, textAlign = TextAlign.Start),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Notifications Section
        SectionHeader("Notifications")
        Spacer(modifier = Modifier.height(20.dp))

        // Toggle
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Notifications", style = TextStyle(fontSize = 16.sp, fontFamily = fontFamily, fontWeight = FontWeight(400), color = primaryBlack))
            Switch(
                checked = profileData.onNotificationChange,
                onCheckedChange = getNotificationChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = waterColor, uncheckedThumbColor = Color.White, uncheckedTrackColor = Color(0xFFE9E9EB), uncheckedBorderColor = Color.Transparent),
                thumbContent = if (profileData.onNotificationChange) { { Icon(imageVector = Icons.Filled.Check, contentDescription = "Notification", modifier = Modifier.size(SwitchDefaults.IconSize)) } } else null
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Reminder Interval
        SettingsRow(title = "Reminder Interval", value = intervalOptions[profileData.selectedIntervalIndex], onClick = { showIntervalDialog = true })
        Spacer(modifier = Modifier.height(4.dp))

        // Wake Up Time
        SettingsRow(title = "Wake Up Time", value = formatTime(profileData.wakeUpHour, profileData.wakeUpMinute), onClick = {})
        Spacer(modifier = Modifier.height(4.dp))

        // Bed Time
        SettingsRow(title = "Bed Time", value = formatTime(profileData.bedHour, profileData.bedMinute), onClick = {})
        Spacer(modifier = Modifier.height(4.dp))

        // Notification Sound
        SettingsRow(title = "Notification Sound", value = soundOptions[profileData.selectedSoundIndex], onClick = { showSoundDialog = true })
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Reminders will be sent every ${intervalOptions[profileData.selectedIntervalIndex]} between ${formatTime(profileData.wakeUpHour, profileData.wakeUpMinute)} and ${formatTime(profileData.bedHour, profileData.bedMinute)}",
            style = TextStyle(fontSize = 12.sp, fontFamily = fontFamily, fontWeight = FontWeight(400), color = Color(0xFF8E8E93)),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Support Section
        SectionHeader("Support")
        Spacer(modifier = Modifier.height(20.dp))

        // Report a Bug
        Row(
            modifier = Modifier.fillMaxWidth().clickable { sendBugReportEmail() }.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Report a Bug", style = TextStyle(fontSize = 16.sp, fontFamily = fontFamily, fontWeight = FontWeight(400), color = primaryBlack))
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos, contentDescription = "Report a Bug", modifier = Modifier.size(14.dp), tint = Color(0xFF8E8E93))
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
            onSelect = { getIntervalChange(it); showIntervalDialog = false },
            onDismiss = { showIntervalDialog = false }
        )
    }

    // Sound Picker Dialog
    if (showSoundDialog) {
        OptionPickerDialog(
            title = "Notification Sound",
            subtitle = "Tap to select a sound",
            options = soundOptions,
            currentIndex = profileData.selectedSoundIndex,
            onSelect = { getSoundChange(it); showSoundDialog = false },
            onDismiss = { showSoundDialog = false }
        )
    }
}

@Composable
private fun SettingsRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = TextStyle(fontSize = 16.sp, fontFamily = fontFamily, fontWeight = FontWeight(400), color = primaryBlack))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, style = TextStyle(fontSize = 14.sp, fontFamily = fontFamily, fontWeight = FontWeight(400), color = Color(0xFF8E8E93)))
            Spacer(modifier = Modifier.width(6.dp))
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos, contentDescription = title, modifier = Modifier.size(14.dp), tint = Color(0xFF8E8E93))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, style = TextStyle(fontSize = 16.sp, fontFamily = fontFamilyBold, fontWeight = FontWeight(400), color = primaryBlack))
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = Color(0xFFD1D1D6)))
    }
}

@Composable
private fun OptionPickerDialog(
    title: String, subtitle: String, options: List<String>, currentIndex: Int,
    onSelect: (Int) -> Unit, onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(16.dp)).padding(24.dp)) {
            Text(text = title, style = TextStyle(fontSize = 20.sp, fontFamily = fontFamilyBold, fontWeight = FontWeight(600), color = primaryBlack))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = TextStyle(fontSize = 14.sp, fontFamily = fontFamily, fontWeight = FontWeight(400), color = Color(0xFF8E8E93)))
            Spacer(modifier = Modifier.height(20.dp))
            options.forEachIndexed { index, label ->
                val isSelected = index == currentIndex
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(index) }
                        .background(if (isSelected) waterColor.copy(alpha = 0.1f) else Color.Transparent, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = label, style = TextStyle(fontSize = 16.sp, fontFamily = fontFamilyLight, fontWeight = if (isSelected) FontWeight(500) else FontWeight(400), color = if (isSelected) waterColor else primaryBlack))
                    if (isSelected) Icon(imageVector = Icons.Filled.Check, contentDescription = "Selected", tint = waterColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text(text = "Cancel", color = Color(0xFF8E8E93), fontFamily = fontFamily, fontSize = 14.sp)
            }
        }
    }
}
