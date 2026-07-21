package com.falcon.hydrohabit.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.onboarding.components.OnBoardingButtons
import com.falcon.hydrohabit.onboarding.components.OnboardingIndicator
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.onboardingBoxColor
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.primaryBlackLight
import com.falcon.hydrohabit.ui.theme.textFieldErrorColor
import com.falcon.hydrohabit.ui.theme.waterColor
import com.falcon.hydrohabit.features.onboarding.utils.activityMeasurementData

@Composable
fun OnBoardingActiveScreen(
    modifier: Modifier = Modifier,
    activityMeasurementData: activityMeasurementData,
    getActiveOutcome: (Int) -> Unit,
    getNavigate: () -> Unit,
    getBacK: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Calculate Amount",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        color = primaryBlackLight,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Please Select your activity level. This helps us calculate the right amount of water",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = fontFamilyLight,
                        fontWeight = FontWeight(200),
                        color = primaryBlackLight,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                )
            }
        }
        item {
            OnboardingIndicator(onboardingNav = 1)
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(onboardingBoxColor, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How Active are you?",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(400),
                        color = primaryBlack,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(15.dp))

                ActivityOption(
                    text = "5-6 Days of workout",
                    isSelected = activityMeasurementData.onActivityOutcome == 0,
                    onClick = { getActiveOutcome(0) }
                )
                ActivityOption(
                    text = "2-3 Days of workout",
                    isSelected = activityMeasurementData.onActivityOutcome == 1,
                    onClick = { getActiveOutcome(1) }
                )
                ActivityOption(
                    text = "Minimal Activity",
                    isSelected = activityMeasurementData.onActivityOutcome == 2,
                    onClick = { getActiveOutcome(2) }
                )

                if (activityMeasurementData.checkError) {
                    Text(
                        text = activityMeasurementData.onErrorText,
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontFamily = fontFamilyLight,
                            fontWeight = FontWeight(200),
                            color = textFieldErrorColor,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OnBoardingButtons(getNavigate, getBacK)
            }
        }
    }
}

@Composable
private fun ActivityOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .border(
                width = 0.5.dp,
                color = if (isSelected) waterColor else Color(0xFFD1D1D6),
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                if (isSelected) waterColor else Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = fontFamilyLight,
                fontWeight = FontWeight(400),
                color = if (isSelected) Color.White else primaryBlack,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.fillMaxWidth().align(Alignment.Center)
        )
    }
}

