package com.falcon.hydrohabit.features.profilescreen

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.falcon.hydrohabit.features.profilescreen.utils.profileData
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyBold
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor
import com.falcon.hydrohabit.ui.theme.waterColorMeter

private val intervalOptions = listOf(
    "30 minutes",
    "1 hour",
    "2 hours",
    "3 hours",
    "4 hours",
)

val intervalMinutesMap = listOf(30, 60, 120, 180, 240)

private fun formatHour(hour: Int): String {
    return when {
        hour == 0 -> "12:00 AM"
        hour < 12 -> "$hour:00 AM"
        hour == 12 -> "12:00 PM"
        else -> "${hour - 12}:00 PM"
    }
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    profileData: profileData,
    getNotificationChange: (Boolean) -> Unit,
    getIntervalChange: (Int) -> Unit,
    getWakeUpHourChange: (Int) -> Unit,
    getBedHourChange: (Int) -> Unit,
) {
    val context = LocalContext.current
    var showIntervalDialog by remember { mutableStateOf(false) }
    var showWakeUpDialog by remember { mutableStateOf(false) }
    var showBedTimeDialog by remember { mutableStateOf(false) }

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
            value = formatHour(profileData.wakeUpHour),
            onClick = { showWakeUpDialog = true }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Bed Time
        SettingsRow(
            title = "Bed Time",
            value = formatHour(profileData.bedHour),
            onClick = { showBedTimeDialog = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Reminders will be sent every ${intervalOptions[profileData.selectedIntervalIndex]} between ${formatHour(profileData.wakeUpHour)} and ${formatHour(profileData.bedHour)}",
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
        val wakeOptions = (4..11).map { formatHour(it) }
        val wakeHours = (4..11).toList()
        val currentIdx = wakeHours.indexOf(profileData.wakeUpHour).coerceAtLeast(0)
        OptionPickerDialog(
            title = "Wake Up Time",
            subtitle = "When do you usually wake up?",
            options = wakeOptions,
            currentIndex = currentIdx,
            onSelect = { index ->
                getWakeUpHourChange(wakeHours[index])
                showWakeUpDialog = false
            },
            onDismiss = { showWakeUpDialog = false }
        )
    }

    // Bed Time Picker Dialog
    if (showBedTimeDialog) {
        val bedOptions = (20..24).map { formatHour(if (it == 24) 0 else it) }
        val bedHours = (20..24).map { if (it == 24) 0 else it }
        val currentIdx = bedHours.indexOf(profileData.bedHour).coerceAtLeast(0)
        OptionPickerDialog(
            title = "Bed Time",
            subtitle = "When do you usually go to bed?",
            options = bedOptions,
            currentIndex = currentIdx,
            onSelect = { index ->
                getBedHourChange(bedHours[index])
                showBedTimeDialog = false
            },
            onDismiss = { showBedTimeDialog = false }
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
            bedHour = 22
        ),
        getNotificationChange = {},
        getIntervalChange = {},
        getWakeUpHourChange = {},
        getBedHourChange = {}
    )
}