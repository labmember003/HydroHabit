package com.falcon.hydrohabit.features.onboarding.presentation.activityMeasurement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.R
import com.falcon.hydrohabit.features.onboarding.utils.activityMeasurementData
import com.falcon.hydrohabit.features.onboarding.utils.OnBoardingButtons
import com.falcon.hydrohabit.features.onboarding.utils.OnboardingIndicator
import com.falcon.hydrohabit.ui.theme.backgroundColor1
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.primaryBlackLight
import com.falcon.hydrohabit.ui.theme.onboardingBoxColor
import com.falcon.hydrohabit.ui.theme.textFieldErrorColor
import com.falcon.hydrohabit.ui.theme.waterColor

@Composable
fun OnBoardingActiveScreen(
    modifier: Modifier = Modifier,
    activityMeasurementData: activityMeasurementData,
    getActiveOutcome: (Int) -> Unit,
    getNavigate: () -> Unit,
    getBacK: () -> Unit,
)
{

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
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
                    ), modifier = Modifier.fillMaxWidth()
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
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }
        }
        item{
            OnboardingIndicator(onboardingNav = 1)
        }

        item{
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        onboardingBoxColor,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
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
                    ), modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(15.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .border(
                            width = 0.5.dp,
                            color = if (activityMeasurementData.onActivityOutcome == 0) waterColor else Color(0xFFD1D1D6),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            if (activityMeasurementData.onActivityOutcome == 0) waterColor else Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            getActiveOutcome(0)
                        },
                ) {
                    Text(
                        text = "5-6 Days of workout",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = fontFamilyLight,
                            fontWeight = FontWeight(400),
                            color = if (activityMeasurementData.onActivityOutcome == 0) Color.White else primaryBlack,
                            textAlign = TextAlign.Center,
                        ), modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .border(
                            width = 0.5.dp,
                            color = if (activityMeasurementData.onActivityOutcome == 1) waterColor else Color(0xFFD1D1D6),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            if (activityMeasurementData.onActivityOutcome == 1) waterColor else Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            getActiveOutcome(1)
                        },
                ) {
                    Text(
                        text = "2-3 Days of workout",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = fontFamilyLight,
                            fontWeight = FontWeight(400),
                            color = if (activityMeasurementData.onActivityOutcome == 1) Color.White else primaryBlack,
                            textAlign = TextAlign.Center,
                        ), modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .border(
                            width = 0.5.dp,
                            color = if (activityMeasurementData.onActivityOutcome == 2) waterColor else Color(0xFFD1D1D6),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            if (activityMeasurementData.onActivityOutcome == 2) waterColor else Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            getActiveOutcome(2)
                        },
                ) {
                    Text(
                        text = "Minimal Activity",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = fontFamilyLight,
                            fontWeight = FontWeight(400),
                            color = if (activityMeasurementData.onActivityOutcome == 2) Color.White else primaryBlack,
                            textAlign = TextAlign.Center,
                        ), modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }

                if (activityMeasurementData.checkError) {

                    Text(
                        text = activityMeasurementData.onErrorText,
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontFamily = fontFamilyLight,
                            fontWeight = FontWeight(200),
                            color = textFieldErrorColor,
                            textAlign = TextAlign.Center,
                        ), modifier = Modifier.fillMaxWidth()
                    )
                }

                OnBoardingButtons(getNavigate, getBacK)
            }
        }



    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewOnBoardingActiveScreen() {
//    var ActiveOutcome by remember {
//        mutableIntStateOf(2)
//    }
//    OnBoardingActiveScreen(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.linearGradient(
//                    start = Offset(Float.POSITIVE_INFINITY, 0f),
//                    end = Offset(0f, Float.POSITIVE_INFINITY),
//                    colors = listOf(backgroundColor1, backgroundColor2)
//                )
//            ),
//        getActiveOutcome = {
//            ActiveOutcome = it
//        },
//        activityMeasurementData = activityMeasurementData(
//            onActivityOutcome = 0,
//            onErrorText = "",
//            checkError = false
//        ), getNavigate = {}, getBacK = {}
//    )
//}
