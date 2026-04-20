package com.falcon.hydrohabit.navigation.navMap

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.falcon.hydrohabit.features.homescreen.HomeScreen
import com.falcon.hydrohabit.features.profilescreen.SettingsScreen
import com.falcon.hydrohabit.features.profilescreen.utils.ProfileData
import com.falcon.hydrohabit.navigation.navUtils.BottomNavScreens
import com.falcon.hydrohabit.platform.KVStorage
import com.falcon.hydrohabit.platform.NotificationScheduler
import com.falcon.hydrohabit.ui.theme.*
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarHostingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onWaterTrackingResourceAmount: Int,
    getWaterTrackingResourceAmount: (Int) -> Unit,
    onTotalWaterTrackingResourceAmount: Int,
    getUpdateTotalWaterTrackingAmount: (Int) -> Unit,
    getReward: (Boolean?) -> Unit,
    onReward: Boolean?,
    onWaterMeterResourceAmount: Int,
    onStreak: String,
    onProgress: String,
    onTime: String,
    getGreeting: () -> Unit,
    isEndless: Boolean = false,
    items: List<Int>,
    streakImages: List<Int>,
    shouldOpenAddWater: Boolean = false,
    onAddWaterHandled: () -> Unit = {},
) {
    val kvStorage: KVStorage = koinInject()
    val notificationScheduler: NotificationScheduler = koinInject()

    var onWaterAddSheet by remember { mutableStateOf(false) }
    var selected by remember { mutableIntStateOf(0) }
    var showBottomBar by remember { mutableStateOf(false) }

    var notificationsEnabled by remember { mutableStateOf(kvStorage.getBoolean("notifications_enabled", false)) }
    var selectedIntervalIndex by remember { mutableIntStateOf(kvStorage.getInt("notification_interval_index", 1)) }
    var wakeUpHour by remember { mutableIntStateOf(kvStorage.getInt("wake_up_hour", 8)) }
    var wakeUpMinute by remember { mutableIntStateOf(kvStorage.getInt("wake_up_minute", 0)) }
    var bedHour by remember { mutableIntStateOf(kvStorage.getInt("bed_hour", 22)) }
    var bedMinute by remember { mutableIntStateOf(kvStorage.getInt("bed_minute", 0)) }
    var selectedSoundIndex by remember { mutableIntStateOf(kvStorage.getInt("notification_sound_index", 0)) }

    fun rescheduleNotifications() {
        if (notificationsEnabled) {
            val intervalMinutes = com.falcon.hydrohabit.features.profilescreen.intervalMinutesMap[selectedIntervalIndex]
            notificationScheduler.scheduleRepeating(intervalMinutes, wakeUpHour, wakeUpMinute, bedHour, bedMinute)
        } else {
            notificationScheduler.cancelAll()
        }
    }

    val listState = rememberLazyListState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isOnHome = currentRoute == BottomNavScreens.HomeScreen.route

    LaunchedEffect(Unit) {
        getUpdateTotalWaterTrackingAmount(onTotalWaterTrackingResourceAmount)
        getGreeting()
        rescheduleNotifications()
    }

    LaunchedEffect(shouldOpenAddWater) {
        if (shouldOpenAddWater) {
            onWaterAddSheet = true
            onAddWaterHandled()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AnimatedVisibility(visible = isOnHome, enter = fadeIn(animationSpec = tween(150)), exit = fadeOut(animationSpec = tween(150))) {
                TopBarLayout(onTime = onTime)
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = !showBottomBar, enter = fadeIn(), exit = fadeOut()) {
                BottomBarLayout(navController)
            }
        },
        floatingActionButton = {
            AnimatedVisibility(visible = !showBottomBar && isOnHome, enter = fadeIn(), exit = fadeOut()) {
                ExtendedFloatingActionButton(
                    onClick = { onWaterAddSheet = !onWaterAddSheet },
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    modifier = Modifier.background(Color.Transparent),
                ) {
                    Box(modifier = Modifier.background(color = waterColor, shape = RoundedCornerShape(16.dp)).size(width = 100.dp, height = 60.dp)) {
                        Text(text = "Add", modifier = Modifier.align(Alignment.Center), style = TextStyle(fontSize = 18.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(400), color = Color.White, textAlign = TextAlign.Center))
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        val padding = it
        NavHost(
            navController = navController,
            startDestination = BottomNavScreens.HomeScreen.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) }
        ) {
            composable(route = BottomNavScreens.HomeScreen.route) {
                HomeScreen(
                    onPad = padding,
                    onWaterTrackingResourceAmount = onWaterTrackingResourceAmount,
                    onTotalWaterTrackingResourceAmount = onTotalWaterTrackingResourceAmount,
                    getReward = getReward,
                    onReward = onReward,
                    getBottomBar = { showBottomBar = it },
                    onWaterMeterResourceAmount = onWaterMeterResourceAmount,
                    onProgress = onProgress,
                    onStreak = onStreak,
                    streakImages = streakImages,
                    modifier = Modifier.fillMaxSize().background(Brush.linearGradient(start = Offset(Float.POSITIVE_INFINITY, 0f), end = Offset(0f, Float.POSITIVE_INFINITY), colors = listOf(backgroundColor1, backgroundColor2)))
                )
            }
            composable(route = BottomNavScreens.SettingsScreen.route) {
                SettingsScreen(
                    profileData = ProfileData(onNotificationChange = notificationsEnabled, selectedIntervalIndex = selectedIntervalIndex, wakeUpHour = wakeUpHour, wakeUpMinute = wakeUpMinute, bedHour = bedHour, bedMinute = bedMinute, selectedSoundIndex = selectedSoundIndex),
                    getNotificationChange = { v -> notificationsEnabled = v; rescheduleNotifications() },
                    getIntervalChange = { v -> selectedIntervalIndex = v; rescheduleNotifications() },
                    getWakeUpHourChange = { h, m -> wakeUpHour = h; wakeUpMinute = m; rescheduleNotifications() },
                    getBedHourChange = { h, m -> bedHour = h; bedMinute = m; rescheduleNotifications() },
                    getSoundChange = { selectedSoundIndex = it },
                    onCustomSoundPicked = {},
                    modifier = Modifier.fillMaxSize().background(Brush.linearGradient(start = Offset(Float.POSITIVE_INFINITY, 0f), end = Offset(0f, Float.POSITIVE_INFINITY), colors = listOf(backgroundColor1, backgroundColor2))).padding(bottom = padding.calculateBottomPadding() / 4, top = padding.calculateTopPadding() / 8)
                )
            }
        }
    }

    if (onWaterAddSheet) {
        Dialog(properties = DialogProperties(), onDismissRequest = { onWaterAddSheet = false }) {
            WaterCarouselSheet(
                modifier = Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(16.dp)),
                listState = listState, isEndless = isEndless, items = items, selected = selected,
                getWaterTrackingResourceAmount = getWaterTrackingResourceAmount,
                getSelected = { selected = it },
                getWaterAddSheet = { onWaterAddSheet = it }
            )
        }
    }
}

@Composable
fun WaterCarouselSheet(
    modifier: Modifier = Modifier, listState: LazyListState, isEndless: Boolean, items: List<Int>, selected: Int,
    getWaterTrackingResourceAmount: (Int) -> Unit, getSelected: (Int) -> Unit, getWaterAddSheet: (Boolean) -> Unit
) {
    val itemHeightDp = 56.dp
    val pickerHeight = itemHeightDp * 3
    val totalDataItems = if (isEndless) Int.MAX_VALUE - 2 else items.size

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            val closestItem = layoutInfo.visibleItemsInfo.filter { it.index in 1..totalDataItems }.minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - viewportCenter) }
            closestItem?.let {
                val dataIndex = (it.index - 1) % items.size
                getSelected(dataIndex)
                listState.animateScrollToItem(it.index, -(layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset - it.size) / 2)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (items.isNotEmpty()) {
            val startIndex = selected + 1
            listState.scrollToItem(startIndex, 0)
        }
    }

    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Add Water", style = TextStyle(fontSize = 20.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(500), color = primaryBlack))
            Text(text = if (selected in items.indices) "${items[selected]} ml" else "", style = TextStyle(fontSize = 20.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(500), color = waterColor))
        }

        Box(modifier = Modifier.fillMaxWidth().height(pickerHeight)) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().height(pickerHeight)) {
                item { Spacer(modifier = Modifier.height(itemHeightDp)) }
                items(count = totalDataItems) {
                    val index = it % items.size
                    val isSelected = index == selected
                    Box(modifier = Modifier.fillMaxWidth().height(itemHeightDp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "${items[index]} ml",
                            style = TextStyle(fontSize = if (isSelected) 28.sp else 20.sp, fontFamily = fontFamilyLight, fontWeight = if (isSelected) FontWeight(600) else FontWeight(400), color = if (isSelected) primaryBlack else primaryBlack.copy(alpha = 0.35f), textAlign = TextAlign.Center)
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(itemHeightDp)) }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { if (selected in items.indices) { getWaterTrackingResourceAmount(items[selected]); getWaterAddSheet(false); getSelected(0) } },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = waterColor)
        ) {
            Text(text = "Done", style = TextStyle(fontSize = 20.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(500), color = Color.White, textAlign = TextAlign.Center))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarLayout(onTime: String) {
    TopAppBar(
        title = {
            Text(text = "$onTime 👋", modifier = Modifier.fillMaxWidth(), style = TextStyle(fontSize = 26.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(200), color = primaryBlack, textAlign = TextAlign.Start))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        modifier = Modifier.padding(top = 8.dp, bottom = 0.dp, start = 8.dp, end = 16.dp).clip(shape = RoundedCornerShape(8.dp))
    )
}

@Composable
fun BottomBarLayout(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val navItems = listOf(BottomNavScreens.HomeScreen, BottomNavScreens.SettingsScreen)

    BottomAppBar(
        modifier = Modifier
            .height(80.dp)
            .clip(shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
            .border(width = 0.5.dp, color = Color(0xFFD1D1D6), shape = RoundedCornerShape(0.dp)),
        containerColor = backgroundColor1
    ) {
        NavigationBar(containerColor = backgroundColor1) {
        navItems.forEach { bottomData ->
            NavigationBarItem(
                selected = bottomData.route == currentRoute,
                onClick = {
                    navController.navigate(bottomData.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = org.jetbrains.compose.resources.painterResource(bottomData.iconRes),
                            contentDescription = bottomData.route,
                            tint = if (currentRoute == bottomData.route) waterColor else Color(0xFF8E8E93),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.animation.AnimatedVisibility(
                            visible = bottomData.route == currentRoute,
                            enter = androidx.compose.animation.expandHorizontally(),
                            exit = androidx.compose.animation.shrinkHorizontally()
                        ) {
                            Text(
                                text = bottomData.route,
                                modifier = Modifier,
                                style = TextStyle(fontSize = 14.sp, fontFamily = fontFamilyLight, fontWeight = FontWeight(200), color = waterColor, textAlign = TextAlign.Start)
                            )
                        }
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(indicatorColor = waterColor.copy(alpha = 0.12f))
            )
        }
    }
    }
}

