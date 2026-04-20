package com.falcon.hydrohabit.features.homescreen.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.falcon.hydrohabit.ui.theme.*

@Composable
fun WaterProgressScreen(
    modifier: Modifier = Modifier,
    onWaterTrackingResourceAmount: Int,
    onWaterMeterResourceAmount: Int,
    onTotalWaterTrackingResourceAmount: Int
) {
    BoxWithConstraints {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val waterPercentageFilled = animateFloatAsState(
            targetValue = onWaterTrackingResourceAmount.toFloat() / onTotalWaterTrackingResourceAmount.toFloat().coerceAtLeast(1f),
            label = "water",
            animationSpec = tween(durationMillis = 1000)
        )
        Box(modifier = modifier) {
            Canvas(
                modifier = Modifier
                    .width(screenWidth * 0.5f)
                    .height(screenHeight * 0.05f)
                    .clip(RoundedCornerShape(40.dp))
                    .border(width = 0.5.dp, color = primaryBlack, shape = RoundedCornerShape(40.dp))
                    .align(Alignment.TopStart)
            ) {
                val width = size.width
                val height = size.height
                val waterWavesYPosition = (waterPercentageFilled.value) * width
                val waterPath = Path().apply {
                    moveTo(x = 0f, y = 0f)
                    lineTo(x = waterWavesYPosition, y = 0f)
                    lineTo(x = waterWavesYPosition, y = height)
                    lineTo(x = 0f, y = height)
                    close()
                }
                drawPath(waterPath, waterColor)
            }
            Text(
                text = "${onWaterMeterResourceAmount}%",
                modifier = Modifier
                    .width(60.dp)
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(500),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}

@Composable
fun StreakScreen(Streak: String, username: String, getStreak: () -> Unit) {
    BoxWithConstraints {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Box(modifier = Modifier) {
            Canvas(
                modifier = Modifier
                    .width(screenWidth * 0.3f)
                    .height(screenHeight * 0.05f)
                    .clip(RoundedCornerShape(40.dp))
                    .border(width = 0.5.dp, color = primaryBlack, shape = RoundedCornerShape(40.dp))
                    .align(Alignment.TopStart)
                    .clickable { getStreak() }
            ) {
                val width = size.width
                val height = size.height
                val waterPath = Path().apply {
                    moveTo(x = 0f, y = 0f)
                    lineTo(x = width, y = 0f)
                    lineTo(x = width, y = height)
                    lineTo(x = 0f, y = height)
                    close()
                }
                drawPath(waterPath, waterColor)
            }
            Text(
                text = username,
                modifier = Modifier
                    .width(screenWidth * 0.25f)
                    .align(Alignment.Center),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(500),
                    color = primaryBlack,
                    textAlign = TextAlign.Start,
                )
            )
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                Text(
                    text = "$Streak \uD83D\uDD25",
                    modifier = Modifier
                        .width(60.dp)
                        .align(Alignment.Center),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(500),
                        color = primaryBlack,
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}

@Composable
fun GlacierScreen(
    onWaterTrackingResourceAmount: Int,
    onTotalWaterTrackingResourceAmount: Int,
    modifier: Modifier = Modifier,
    screenWidth: Dp = 300.dp,
    screenHeight: Dp = 400.dp,
) {
    val waterPercentageFilled = animateFloatAsState(
        targetValue = onWaterTrackingResourceAmount.toFloat() / onTotalWaterTrackingResourceAmount.toFloat().coerceAtLeast(1f),
        label = "glacier",
        animationSpec = tween(durationMillis = 1000)
    )

    Column(
        modifier = modifier.size(screenWidth, screenHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Target : ${onTotalWaterTrackingResourceAmount} ml",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                )
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Glacier(
            screenHeight = screenHeight,
            screenWidth = screenWidth,
            waterPercentageFilled = waterPercentageFilled,
            modifier = Modifier.aspectRatio(0.8f)
        )
    }
}

@Composable
fun Glacier(
    screenWidth: Dp,
    screenHeight: Dp,
    waterPercentageFilled: State<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.offset(x = 30.dp)) {
        val width = size.width - 150f
        val height = size.height
        val waterWavesYPosition = (1 - waterPercentageFilled.value) * width
        val waterPath = Path().apply {
            moveTo(x = width / 2, y = 0f)
            lineTo(x = width * 0.56f, y = height * 0.08f)
            lineTo(x = width * 0.69f, y = height * 0.13f)
            lineTo(x = width * 0.77f, y = height * 0.21f)
            lineTo(x = width * 0.84f, y = height * 0.20f)
            lineTo(x = width * 0.90f, y = height * 0.24f)
            lineTo(x = width * 0.93f, y = height * 0.34f)
            lineTo(x = width * 0.82f, y = height * 0.64f)
            lineTo(x = width * 0.76f, y = height * 0.69f)
            lineTo(x = width * 0.74f, y = height * 0.85f)
            lineTo(x = width * 0.55f, y = height)
            lineTo(x = width * 0.20f, y = height * 0.70f)
            lineTo(x = width * 0.08f, y = height * 0.30f)
            lineTo(x = width * 0.21f, y = height * 0.17f)
            lineTo(x = width * 0.32f, y = height * 0.12f)
            lineTo(x = width * 0.33f, y = height * 0.08f)
            close()
        }
        drawPath(waterPath, Color.Black, style = Stroke(width = 0.8f))
        clipPath(waterPath) {
            drawRect(color = Color(0XFFEFEFEF), size = size)
            val waterFillPath = Path().apply {
                moveTo(x = 0f, y = waterWavesYPosition)
                lineTo(x = size.width, y = waterWavesYPosition)
                lineTo(x = size.width, y = size.height)
                lineTo(x = 0f, y = size.height)
                close()
            }
            drawPath(waterFillPath, waterColor)
        }
    }
}

@Composable
fun IntakeProgressScreen(modifier: Modifier = Modifier, onAvgWaterPercent: String, onAvgWaterIntake: String) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = onAvgWaterIntake,
                style = TextStyle(fontSize = 18.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(400), color = Color.White, textAlign = TextAlign.Start)
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = onAvgWaterPercent,
                style = TextStyle(fontSize = 42.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(700), color = Color.White, textAlign = TextAlign.Center)
            )
        }
    }
}

@Composable
fun WeekScreen(modifier: Modifier = Modifier, weekStreak: List<String>) {
    Box(modifier = modifier) {
        androidx.compose.foundation.lazy.LazyRow(
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
                        modifier = Modifier.width(220.dp).align(Alignment.Center),
                        style = TextStyle(fontSize = 16.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(400), color = primaryBlack, textAlign = TextAlign.Center)
                    )
                }
                Spacer(modifier = Modifier.size(5.dp))
            }
        }
    }
}
