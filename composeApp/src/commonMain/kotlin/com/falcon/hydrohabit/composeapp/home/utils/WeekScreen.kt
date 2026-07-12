package com.falcon.hydrohabit.composeapp.home.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.composeapp.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.composeapp.ui.theme.onboardingBoxColor
import com.falcon.hydrohabit.composeapp.ui.theme.primaryBlack

@Composable
fun WeekScreen(modifier: Modifier = Modifier, weekStreak: List<String>) {
    Box(modifier = modifier) {
        LazyRow(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.align(Alignment.Center)
        ) {
            items(weekStreak.size) {
                Spacer(modifier = Modifier.size(5.dp))
                Box(
                    modifier = Modifier
                        .background(onboardingBoxColor, shape = RoundedCornerShape(50.dp))
                        .size(40.dp)
                ) {
                    Text(
                        text = weekStreak[it],
                        modifier = Modifier
                            .width(220.dp)
                            .align(Alignment.Center),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = fontFamilyLight,
                            fontWeight = FontWeight(400),
                            color = primaryBlack,
                            textAlign = TextAlign.Center,
                        )
                    )
                }
                Spacer(modifier = Modifier.size(5.dp))
            }
        }
    }
}
