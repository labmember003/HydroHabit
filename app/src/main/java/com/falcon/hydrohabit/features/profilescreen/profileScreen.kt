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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.falcon.hydrohabit.features.profilescreen.utils.profileData
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyBold
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor
import com.falcon.hydrohabit.ui.theme.waterColorMeter
import androidx.core.net.toUri

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    profileData: profileData,
    getNotificationChange: (Boolean) -> Unit,
    getNotificationIntervals: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Notifications",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = fontFamilyBold,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                ), modifier = Modifier
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color(0xFFD1D1D6))
            )
        }
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
                    textAlign = TextAlign.Center,
                ), modifier = Modifier
            )
            Spacer(modifier = Modifier.width(10.dp))
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
        Spacer(modifier = Modifier.height(16.dp))

        // Notification Intervals
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { getNotificationIntervals() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Notification Intervals",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                ), modifier = Modifier
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = "Notification Intervals",
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF8E8E93)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Support Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Support",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = fontFamilyBold,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                ), modifier = Modifier
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color(0xFFD1D1D6))
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Report a Bug
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    sendBugReportEmail(context)
                }
                .padding(vertical = 8.dp),
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
                    textAlign = TextAlign.Center,
                ), modifier = Modifier
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = "Report a Bug",
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF8E8E93)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))
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
        // Fallback: open chooser
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
            onNotificationChange = false
        ),
        getNotificationChange = {},
        getNotificationIntervals = {}
    )
}