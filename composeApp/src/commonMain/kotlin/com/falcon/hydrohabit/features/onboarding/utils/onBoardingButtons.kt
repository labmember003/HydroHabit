package com.falcon.hydrohabit.features.onboarding.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.onboardingBoxColor
import com.falcon.hydrohabit.ui.theme.waterColor

@Composable
fun OnBoardingButtons(getNavigate: () -> Unit, getBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(90.dp)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .background(onboardingBoxColor, shape = RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { getBack() }
                ),
        ) {
            Text(
                text = "Back",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.fillMaxWidth().align(Alignment.Center)
            )
        }
        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { getNavigate() }
                )
                .weight(1f)
                .height(90.dp)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .background(waterColor, shape = RoundedCornerShape(10.dp)),
        ) {
            Text(
                text = "Done",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.fillMaxWidth().align(Alignment.Center)
            )
        }
    }
}

