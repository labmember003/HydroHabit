package com.falcon.hydrohabit.features.onboarding.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor

@Composable
fun PermissionDeniedAlertDialog(
    getDismissButton: () -> Unit,
    getConfirmButton: () -> Unit,
    getAppSettings: () -> Unit,
    onPermissionTitle: String,
    onPermissionText: String,
    onPermanentlyDenied: Boolean
) {
    Dialog(onDismissRequest = { getDismissButton() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = onPermissionTitle,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = onPermissionText,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (onPermanentlyDenied) {
                TextButton(onClick = { getAppSettings() }) {
                    Text("Open Settings", color = waterColor, fontFamily = fontFamily)
                }
            }

            TextButton(onClick = { getDismissButton() }) {
                Text("Skip", color = Color(0xFF8E8E93), fontFamily = fontFamily)
            }
        }
    }
}

