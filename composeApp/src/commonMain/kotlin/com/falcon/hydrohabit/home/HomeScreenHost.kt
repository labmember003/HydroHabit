package com.falcon.hydrohabit.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.falcon.hydrohabit.alarm.AlarmSchedulerContract
import com.falcon.hydrohabit.ui.theme.backgroundColor1
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor
import com.falcon.hydrohabit.features.homescreen.usecase.calculateWaterPercent
import com.falcon.hydrohabit.features.homescreen.usecase.currentTimeInfo
import com.falcon.hydrohabit.features.homescreen.usecase.getGreeting
import com.falcon.hydrohabit.features.homescreen.usecase.getStreakMessage
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import com.falcon.hydrohabit.model.water_reminder.WaterReminder
import com.falcon.hydrohabit.tracking.HydrationTracker
import com.falcon.hydrohabit.ui.DroplySnackbarHost
import com.falcon.hydrohabit.tracking.formatMl
import hydrohabit.composeapp.generated.resources.Res
import hydrohabit.composeapp.generated.resources.day1
import hydrohabit.composeapp.generated.resources.day2
import hydrohabit.composeapp.generated.resources.day3
import hydrohabit.composeapp.generated.resources.day4
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenHost(
    onboardingRepo: OnboardingRepositoryContract,
    alarmScheduler: AlarmSchedulerContract,
    tracker: HydrationTracker,
    notificationsEnabled: Boolean,
    onShowMessage: (message: String, actionLabel: String?, onAction: (() -> Unit)?) -> Unit,
    snackbarHostState: SnackbarHostState,
    /** Consecutive-day streak from the intake log; null falls back to the stored counter. */
    streakOverride: Int? = null,
    shouldOpenAddWater: Boolean = false,
    onAddWaterHandled: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    // --- State: everything below is read from the stores, which HydrationTracker updates ---
    var usedWaterAmount by remember { mutableIntStateOf(0) }
    var totalWaterAmount by remember { mutableIntStateOf(0) }
    var waterPercent by remember { mutableIntStateOf(0) }
    var rewardDialog by remember { mutableStateOf(false) }
    var streakScore by remember { mutableIntStateOf(0) }
    val perks = remember { mutableStateListOf<DrawableResource>() }
    var onProgress by remember { mutableStateOf("") }
    var onTime by remember { mutableStateOf("") }
    var showBottomBar by remember { mutableStateOf(false) }
    var onWaterAddSheet by remember { mutableStateOf(false) }

    val waterItems = listOf(50, 100, 200, 250, 300, 400, 500)
    var selectedWaterIndex by remember { mutableIntStateOf(3) }
    val listState = rememberLazyListState()

    // --- Greeting ---
    LaunchedEffect(Unit) {
        onTime = getGreeting(currentTimeInfo().hour)
    }

    // --- Water amount (resets at the start of a new day) ---
    LaunchedEffect(Unit) {
        onboardingRepo.getWaterAmount().collect { wa ->
            val todayDate = currentTimeInfo().dateString
            if (todayDate != wa.onWaterDay) {
                usedWaterAmount = 0
                totalWaterAmount = wa.onTotalWater
                waterPercent = 0
                onboardingRepo.updateWaterAmount(0, wa.onTotalWater, todayDate)
            } else {
                usedWaterAmount = wa.onUsedWater
                totalWaterAmount = wa.onTotalWater
                waterPercent = calculateWaterPercent(wa.onUsedWater, wa.onTotalWater)
            }
        }
    }

    // A one-off nudge two hours after the latest drink, only if reminders are on.
    LaunchedEffect(usedWaterAmount, notificationsEnabled) {
        if (notificationsEnabled && usedWaterAmount in 1 until totalWaterAmount) {
            alarmScheduler.schedule(
                WaterReminder.inTwoHours("You've had $usedWaterAmount of $totalWaterAmount ml today. Time for another glass?")
            )
        }
    }

    // --- Streak ---
    LaunchedEffect(Unit) {
        onboardingRepo.getStreak().collect { s ->
            streakScore = s.streak
            perks.clear()
            s.perks.distinct().forEach { perks.add(perkIdToResource(it)) }
        }
    }

    // --- Update progress message ---
    LaunchedEffect(waterPercent) {
        onProgress = getStreakMessage(waterPercent)
    }

    // --- Open add water dialog when triggered from notification ---
    LaunchedEffect(shouldOpenAddWater) {
        if (shouldOpenAddWater) {
            onWaterAddSheet = true
            onAddWaterHandled()
        }
    }

    // --- Add water: log it, celebrate when the goal is reached, offer Undo ---
    fun fillWaterUpdate(waterUpdate: Int) {
        scope.launch {
            val change = tracker.addWater(waterUpdate)
            if (change.reachedGoal) rewardDialog = true
            onShowMessage("Added ${formatMl(waterUpdate)}", "Undo") {
                scope.launch { tracker.remove(change.entry.id) }
            }
        }
    }

    // --- UI ---
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "$onTime 👋",
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(
                                fontSize = 26.sp,
                                fontFamily = fontFamilyLight,
                                fontWeight = FontWeight(200),
                                color = primaryBlack,
                                textAlign = TextAlign.Start,
                            )
                        )
                    },
                    actions = {},
                    colors = TopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = Color.Transparent,
                        titleContentColor = Color.Transparent,
                        actionIconContentColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 0.dp, start = 8.dp, end = 16.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !showBottomBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = { onWaterAddSheet = !onWaterAddSheet },
                    containerColor = Color.Transparent,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp
                    ),
                    contentColor = Color.White,
                    modifier = Modifier.background(Color.Transparent),
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = waterColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .size(width = 100.dp, height = 60.dp)
                    ) {
                        Text(
                            text = "Add",
                            modifier = Modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = fontFamilyLight,
                                fontWeight = FontWeight(400),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { DroplySnackbarHost(snackbarHostState) },
    ) { padding ->
        HomeScreen(
            onPad = padding,
            onWaterTrackingResourceAmount = usedWaterAmount,
            onWaterMeterResourceAmount = waterPercent,
            onTotalWaterTrackingResourceAmount = totalWaterAmount,
            getReward = { if (it != null) rewardDialog = it },
            onReward = rewardDialog,
            getBottomBar = { showBottomBar = it },
            onStreak = (streakOverride ?: streakScore).toString(),
            onProgress = onProgress,
            streakImages = perks.toList(),
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        start = Offset(Float.POSITIVE_INFINITY, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY),
                        colors = mutableListOf(backgroundColor1, backgroundColor2)
                    )
                )
        )
    }

    // --- Water picker dialog ---
    if (onWaterAddSheet) {
        Dialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = true
            ),
            onDismissRequest = { onWaterAddSheet = false }
        ) {
            WaterCarouselSheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp)),
                listState = listState,
                isEndless = false,
                items = waterItems,
                selected = selectedWaterIndex,
                getWaterTrackingResourceAmount = { amount ->
                    fillWaterUpdate(amount)
                },
                getSelected = { selectedWaterIndex = it },
                getWaterAddSheet = { onWaterAddSheet = it }
            )
        }
    }
}

@Composable
fun WaterCarouselSheet(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    isEndless: Boolean,
    items: List<Int>,
    selected: Int,
    getWaterTrackingResourceAmount: (Int) -> Unit,
    getSelected: (Int) -> Unit,
    getWaterAddSheet: (Boolean) -> Unit
) {
    val visibleItems = 3
    val itemHeightDp = 56.dp
    val pickerHeight = itemHeightDp * visibleItems
    val totalDataItems = if (isEndless) Int.MAX_VALUE - 2 else items.size

    // Haptic feedback each time a new item passes the center while scrolling.
    val tickHaptic = rememberCarouselHaptic()
    val lastCenteredIndex = remember { mutableIntStateOf(-1) }
    LaunchedEffect(
        remember { derivedStateOf { listState.firstVisibleItemIndex } },
        listState.firstVisibleItemScrollOffset
    ) {
        val layoutInfo = listState.layoutInfo
        val viewportCenter =
            (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
        val closestItem = layoutInfo.visibleItemsInfo
            .filter { it.index in 1..totalDataItems }
            .minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - viewportCenter) }
        closestItem?.let {
            val dataIndex = (it.index - 1) % items.size
            if (lastCenteredIndex.intValue != dataIndex) {
                lastCenteredIndex.intValue = dataIndex
                tickHaptic()
            }
        }
    }

    // Snap to the center item when scrolling stops. Only act after a real scroll —
    // otherwise the initial (stale) scroll position would overwrite `selected` and
    // desync the header/Done amount from the visually centered item.
    var hasScrolled by remember { mutableStateOf(false) }
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            hasScrolled = true
        } else if (hasScrolled) {
            val layoutInfo = listState.layoutInfo
            val viewportCenter =
                (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            val closestItem = layoutInfo.visibleItemsInfo
                .filter { it.index in 1..totalDataItems }
                .minByOrNull {
                    kotlin.math.abs((it.offset + it.size / 2) - viewportCenter)
                }
            closestItem?.let {
                val dataIndex = (it.index - 1) % items.size
                getSelected(dataIndex)
                listState.animateScrollToItem(
                    it.index,
                    -(layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset - it.size) / 2
                )
            }
        }
    }

    // Scroll to selected item on first composition
    LaunchedEffect(Unit) {
        if (items.isNotEmpty()) {
            val startIndex = if (isEndless) {
                val midpoint = (totalDataItems / 2)
                midpoint - (midpoint % items.size) + selected + 1
            } else {
                selected + 1
            }
            listState.scrollToItem(startIndex, 0)
            val layoutInfo = listState.layoutInfo
            val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
            val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == startIndex }
            itemInfo?.let {
                listState.scrollToItem(startIndex, -(viewportHeight - it.size) / 2)
            }
        }
    }

    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Add Water",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(500),
                    color = primaryBlack,
                )
            )
            Text(
                text = if (selected in items.indices) "${items[selected]} ml" else "",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(500),
                    color = waterColor,
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pickerHeight)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(itemHeightDp)
                    .background(
                        waterColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = waterColor.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(pickerHeight)
            ) {
                item {
                    Spacer(modifier = Modifier.height(itemHeightDp))
                }
                items(
                    count = totalDataItems,
                    itemContent = {
                        val index = it % items.size
                        val isSelected = index == selected
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeightDp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${items[index]} ml",
                                style = TextStyle(
                                    fontSize = if (isSelected) 28.sp else 20.sp,
                                    fontFamily = fontFamilyLight,
                                    fontWeight = if (isSelected) FontWeight(600) else FontWeight(
                                        400
                                    ),
                                    color = if (isSelected) primaryBlack else primaryBlack.copy(
                                        alpha = 0.35f
                                    ),
                                    textAlign = TextAlign.Center,
                                )
                            )
                        }
                    }
                )
                item {
                    Spacer(modifier = Modifier.height(itemHeightDp))
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(itemHeightDp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.White, Color.White.copy(alpha = 0f))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(itemHeightDp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0f), Color.White)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (selected in items.indices) {
                    getWaterTrackingResourceAmount(items[selected])
                    getWaterAddSheet(false)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonColors(
                containerColor = waterColor,
                contentColor = Color.White,
                disabledContainerColor = waterColor.copy(alpha = 0.5f),
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = "Done",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(500),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}

// --- Perk mapping: R.drawable IDs ↔ Compose Resources ---
// The original app stores perks as Android R.drawable int IDs in DataStore.
// We map them to/from DrawableResource for CMP.

private val perkMap = mapOf(
    1 to Res.drawable.day1,
    2 to Res.drawable.day2,
    3 to Res.drawable.day3,
    4 to Res.drawable.day4,
)

fun perkIdToResource(id: Int): DrawableResource {
    return perkMap[id] ?: Res.drawable.day1
}

fun resourceToPerkId(res: DrawableResource): Int {
    return perkMap.entries.firstOrNull { it.value == res }?.key ?: 1
}
