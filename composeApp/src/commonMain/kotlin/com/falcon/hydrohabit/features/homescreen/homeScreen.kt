package com.falcon.hydrohabit.features.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.falcon.hydrohabit.features.homescreen.utils.*
import com.falcon.hydrohabit.ui.theme.*
import androidx.compose.ui.unit.times

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPad: PaddingValues,
    onWaterTrackingResourceAmount: Int,
    onWaterMeterResourceAmount: Int,
    onTotalWaterTrackingResourceAmount: Int,
    modifier: Modifier = Modifier,
    getReward: (Boolean?) -> Unit,
    onReward: Boolean?,
    getBottomBar: (Boolean) -> Unit,
    onStreak: String,
    onProgress: String,
    streakImages: List<Int>
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lazyListState = rememberLazyListState()
    val showBottombar by remember{
        derivedStateOf{
            lazyListState.firstVisibleItemIndex !=0
        }
    }

    var showStreakCollector by remember{
        mutableStateOf(false)
    }

    LaunchedEffect(showBottombar){
        getBottomBar(showBottombar)
    }


        if (onReward == true) {
            DialogRewardScreen(getReward = {
                getReward(it)
            })
        }


    Box(modifier = modifier
        .padding(
            top = onPad.calculateTopPadding(), bottom = onPad.calculateBottomPadding()
        )) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top

                ) {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.Start
                    ) {

                        WaterProgressScreen(
                            onWaterTrackingResourceAmount = onWaterTrackingResourceAmount,
                            onWaterMeterResourceAmount = onWaterMeterResourceAmount,
                            onTotalWaterTrackingResourceAmount = onTotalWaterTrackingResourceAmount,
                            modifier = Modifier
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = onProgress,
                            modifier = Modifier.width(220.dp),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = fontFamilyLight,
                                fontWeight = FontWeight(400),
                                color = primaryBlack,
                                textAlign = TextAlign.Start,
                            )
                        )


                    }
                    StreakScreen(Streak = onStreak, username = "Streak", getStreak = {
                        showStreakCollector = true
                    })

                }
            }
            item {
                BoxWithConstraints {
                    val screenWidth = maxWidth
                    val screenHeight = maxHeight
                    GlacierScreen(
                        onWaterTrackingResourceAmount = onWaterTrackingResourceAmount,
                        onTotalWaterTrackingResourceAmount = onTotalWaterTrackingResourceAmount,
                        screenWidth = screenWidth.value*0.8.dp,
                        screenHeight = screenHeight.value * 0.6.dp
                    )
                }
            }
        }

    }


if(showStreakCollector){
    ModalBottomSheet(
        containerColor = backgroundColor1,
        sheetState = sheetState,
        onDismissRequest = {showStreakCollector = !showStreakCollector}) {
            StreakSheet(streak = streakImages, modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .padding(10.dp))
        }
    }
}

@Composable
fun StreakSheet(streak: List<Int>, modifier: Modifier = Modifier) {

    data class Achievement(
        val title: String,
        val description: String,
        val emoji: String,
        val requiredDays: Int
    )

    val achievements = listOf(
        Achievement("First Drop", "Complete your first day", "💧", 1),
        Achievement("3-Day Flow", "Stay hydrated for 3 days", "🌊", 3),
        Achievement("Week Warrior", "7 day streak", "⚡", 7),
        Achievement("Two Week Tide", "14 day streak", "🌿", 14),
        Achievement("Monthly Master", "30 day streak", "🏆", 30),
        Achievement("60-Day Legend", "60 day streak", "👑", 60),
        Achievement("100-Day Hero", "100 day streak", "💎", 100),
        Achievement("365-Day Champion", "Full year streak!", "🔥", 365),
    )

    val currentStreak = streak.size

    Box(modifier = modifier) {
        Column {
            Text(
                text = "Achievements",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(600),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                )
            )
            Text(
                text = "Complete daily water goals to unlock badges",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                style = TextStyle(
                    fontSize = 13.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(400),
                    color = primaryBlack.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                )
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = {
                    items(achievements.size) { index ->
                        val achievement = achievements[index]
                        val unlocked = currentStreak >= achievement.requiredDays
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (unlocked) waterColor.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.06f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (unlocked) waterColor.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (unlocked) achievement.emoji else "🔒",
                                    fontSize = 32.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = achievement.title,
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = fontFamilyLight,
                                        fontWeight = FontWeight(600),
                                        color = if (unlocked) primaryBlack else primaryBlack.copy(alpha = 0.35f),
                                        textAlign = TextAlign.Center,
                                    )
                                )
                                Text(
                                    text = achievement.description,
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        fontFamily = fontFamilyLight,
                                        fontWeight = FontWeight(400),
                                        color = if (unlocked) primaryBlack.copy(alpha = 0.6f) else primaryBlack.copy(alpha = 0.25f),
                                        textAlign = TextAlign.Center,
                                    ),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                if (unlocked) {
                                    Text(
                                        text = "✓ Unlocked",
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            fontFamily = fontFamilyLight,
                                            fontWeight = FontWeight(500),
                                            color = waterColor,
                                            textAlign = TextAlign.Center,
                                        ),
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${achievement.requiredDays - currentStreak} days to go",
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            fontFamily = fontFamilyLight,
                                            fontWeight = FontWeight(400),
                                            color = primaryBlack.copy(alpha = 0.3f),
                                            textAlign = TextAlign.Center,
                                        ),
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }

    }
}

@Composable
fun DialogRewardScreen(getReward: (Boolean) -> Unit) {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        RewardScreen(
            getNavigate = {
                getReward(false)
            }, modifier = Modifier
                .background(
                    blackShadeColor.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(10.dp)
                )
                .fillMaxWidth(0.9f)
                .height(400.dp)
                .padding(16.dp)
        )
    }
}

