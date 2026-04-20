package com.falcon.hydrohabit.features.onboarding.presentation.bodyMeasurement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.features.onboarding.utils.BodyMeasurementData
import com.falcon.hydrohabit.features.onboarding.utils.OnboardingIndicator
import com.falcon.hydrohabit.features.onboarding.utils.SingleButton
import com.falcon.hydrohabit.features.onboarding.utils.TextFieldCustom
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.onboardingBoxColor
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.primaryBlackLight

@Composable
fun OnBoardingBodyMeasurementsScreen(
    getWeightChange: (String) -> Unit,
    getHeightChange: (String) -> Unit,
    getNavigate: () -> Unit,
    modifier: Modifier = Modifier,
    bodyMeasurementData: BodyMeasurementData
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier.wrapContentSize().fillMaxWidth().padding(horizontal = 16.dp),
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
                    text = "Please add your body measurements. This helps us calculate the right amount of water",
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
        item { OnboardingIndicator(onboardingNav = 0) }
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
                    text = "Body Measurements",
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
                TextFieldCustom(
                    onTextChange = bodyMeasurementData.onWeightChange,
                    checkError = bodyMeasurementData.onWeightCheck,
                    onErrorText = bodyMeasurementData.onWeightError,
                    getTextChange = { getWeightChange(it) },
                    onPlaceHolderText = "Weight",
                    onLabelText = "Weight (kg)",
                    onImeAction = ImeAction.Next,
                    onKeyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextFieldCustom(
                    onTextChange = bodyMeasurementData.onHeightChange,
                    checkError = bodyMeasurementData.onHeightCheck,
                    onErrorText = bodyMeasurementData.onHeightError,
                    getTextChange = { getHeightChange(it) },
                    onPlaceHolderText = "Height",
                    onLabelText = "Height (cm)",
                    onImeAction = ImeAction.Done,
                    onKeyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(15.dp))
                SingleButton(getNavigate = getNavigate, buttonName = "Next")
            }
        }
    }
}

