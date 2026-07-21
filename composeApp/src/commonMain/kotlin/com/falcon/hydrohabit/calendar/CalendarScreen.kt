package com.falcon.hydrohabit.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.falcon.hydrohabit.ui.theme.backgroundColor1
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    onMonth: String,
    calendarList: List<List<Color>>,
    onAvgIntake: String,
    onBestStreak: String,
    onWeight: String,
    onHeight: String,
    intakeAmount: String
) {
    Box(modifier = modifier.padding(vertical = 16.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Your Monthly Report",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Start,
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .background(
                        primaryBlack,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.35f)
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = onMonth,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = fontFamilyLight,
                            fontWeight = FontWeight(400),
                            color = backgroundColor1,
                            textAlign = TextAlign.Center,
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(userScrollEnabled = false) {
                        items(calendarList.size) { row ->
                            Spacer(modifier = Modifier.size(4.dp))
                            LazyRow(userScrollEnabled = false) {
                                items(calendarList[row].size) { col ->
                                    val color = calendarList[row][col]
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .height(30.dp)
                                            .width(30.dp)
                                            .border(
                                                width = 1.dp,
                                                color = if (color != waterColor) Color.White else primaryBlack,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                    )
                                    Spacer(modifier = Modifier.size(4.dp))
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Stats",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Start,
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            UserValues(
                onAvgIntake = onAvgIntake,
                onWeight = onWeight,
                onHeight = onHeight,
                onBestStreak = onBestStreak,
                intakeAmount = intakeAmount
            )
        }
    }
}

@Composable
private fun UserValues(
    onAvgIntake: String,
    onBestStreak: String,
    onWeight: String,
    onHeight: String,
    intakeAmount: String
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatCard(label = "Intake Amt.", value = intakeAmount)
            StatCard(label = "Best Streak", value = onBestStreak)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatCard(label = "Weight", value = onWeight)
            StatCard(label = "Height", value = onHeight)
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Box(
        modifier = Modifier
            .height(110.dp)
            .width(150.dp)
            .background(waterColor, shape = RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 35.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(600),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}
