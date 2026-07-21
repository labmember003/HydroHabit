package com.falcon.hydrohabit.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.settings.rememberNotificationPermissionState
import com.falcon.hydrohabit.ui.theme.backgroundColor1
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.onboardingBoxColor
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.primaryBlackLight
import com.falcon.hydrohabit.ui.theme.waterColor

@Composable
fun OnBoardingNotificationScreen(
    modifier: Modifier = Modifier,
    onFinished: (granted: Boolean) -> Unit
) {
    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionState = rememberNotificationPermissionState(
        onPermissionResult = { granted ->
            if (granted) {
                onFinished(true)
            } else {
                showPermissionDialog = true
            }
        }
    )

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            containerColor = backgroundColor1,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint = waterColor
                )
            },
            titleContentColor = primaryBlack,
            textContentColor = primaryBlackLight,
            title = { Text("Notification Permission") },
            text = { Text("Notification permission was denied. Notifications are necessary to remind you of your water breaks. You can enable it later in app settings.") },
            confirmButton = {
                Button(onClick = {
                    showPermissionDialog = false
                    permissionState.openAppSettings()
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    onFinished(false)
                }) {
                    Text("Later")
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reminder to Drink",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    color = primaryBlackLight,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Every few hours we will remind you to drink water",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(200),
                    color = primaryBlackLight,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(onboardingBoxColor, shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Notifications",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(500),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "To remind you to drink water while you are away, we need permission to show notifications.",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { permissionState.requestPermission() }
                    )
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(waterColor, shape = RoundedCornerShape(10.dp)),
            ) {
                Text(
                    text = "Allow",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(500),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
        }
    }
}
