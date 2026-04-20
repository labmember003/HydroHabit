package com.falcon.hydrohabit.features.onboarding.presentation.permissionScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.features.onboarding.utils.SingleButton
import com.falcon.hydrohabit.ui.theme.*

@Composable
fun OnboardingNotifications(
    modifier: Modifier = Modifier,
    getAllow: () -> Unit,
    onPermissionDenied: Boolean,
    getPermissionDenied: (Boolean) -> Unit
) {
    // On iOS, notification permission will be requested natively
    // This screen just shows info and proceeds
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reminder to Drink",
                style = TextStyle(fontSize = 18.sp, fontFamily = fontFamily, fontWeight = FontWeight(600), color = primaryBlackLight, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Every few hours we will remind you to drink water",
                style = TextStyle(fontSize = 15.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(200), color = primaryBlackLight, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                .background(onboardingBoxColor, shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Notifications",
                style = TextStyle(fontSize = 16.sp, fontFamily = fontFamily, fontWeight = FontWeight(500), color = primaryBlack, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "To remind you to drink water while you are away, we need permission to show notifications.",
                style = TextStyle(fontSize = 15.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(400), color = primaryBlack, textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            SingleButton(getNavigate = { getAllow() }, buttonName = "Allow")
        }
    }
}

