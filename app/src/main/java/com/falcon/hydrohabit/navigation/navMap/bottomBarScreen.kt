package com.falcon.hydrohabit.navigation.navMap

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.falcon.hydrohabit.R
import com.falcon.hydrohabit.alarmSchedular.AlarmScheduler
import com.falcon.hydrohabit.features.homescreen.HomeScreen
import com.falcon.hydrohabit.features.profilescreen.SettingsScreen
import com.falcon.hydrohabit.features.profilescreen.utils.profileData
import com.falcon.hydrohabit.ui.theme.backgroundColor1
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor
import com.falcon.hydrohabit.navigation.navUtils.BottomNavScreens
import kotlinx.coroutines.delay
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarHostingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onWaterTrackingResourceAmount: Int,
    getWaterTrackingResourceAmount: (Int) -> Unit,
    onTotalWaterTrackingResourceAmount: Int,
    getUpdateTotalWaterTrackingAmount: (Int) -> Unit,
    onUserName: String,
    getReward: (Boolean?) -> Unit,
    onReward: Boolean?,
    onWaterMeterResourceAmount: Int,
    onStreak: String,
    onProgress: String,
    onTime: String,
    getGreeting: () -> Unit,
    isEndless: Boolean = true,
    items: List<Int>,
    streakImages: List<Int>,
) {
    var onAdd by remember {
        mutableStateOf(false)
    }
    var onWaterAddSheet by remember {
        mutableStateOf(false)
    }
    var onHome by remember {
        mutableStateOf(true)
    }
    var onTitleChage by remember {
        mutableStateOf(false)
    }
    var selected by remember {
        mutableIntStateOf(0)
    }
    var showBottomBar by remember {
        mutableStateOf(false)
    }
    val context = navController.context
    val prefs = remember { context.getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE) }
    val alarmScheduler = remember { AlarmScheduler(context) }
    var notificationsEnabled by remember {
        mutableStateOf(prefs.getBoolean("notifications_enabled", false))
    }
    var selectedIntervalIndex by remember {
        mutableIntStateOf(prefs.getInt("notification_interval_index", 1))
    }
    var wakeUpHour by remember {
        mutableIntStateOf(prefs.getInt("wake_up_hour", 8))
    }
    var wakeUpMinute by remember {
        mutableIntStateOf(prefs.getInt("wake_up_minute", 0))
    }
    var bedHour by remember {
        mutableIntStateOf(prefs.getInt("bed_hour", 22))
    }
    var bedMinute by remember {
        mutableIntStateOf(prefs.getInt("bed_minute", 0))
    }
    var selectedSoundIndex by remember {
        mutableIntStateOf(prefs.getInt("notification_sound_index", 0))
    }

    // Helper to schedule or cancel notifications based on current settings
    fun rescheduleNotifications() {
        if (notificationsEnabled) {
            val intervalMinutes = com.falcon.hydrohabit.features.profilescreen.intervalMinutesMap[selectedIntervalIndex]
            alarmScheduler.scheduleRepeating(intervalMinutes, wakeUpHour, bedHour)
        } else {
            alarmScheduler.cancelAll()
        }
    }
    val navItems = mutableListOf(
        BottomNavScreens.HomeScreen,
        BottomNavScreens.SettingsScreen
    )
    val listState = rememberLazyListState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isOnHome = currentRoute == BottomNavScreens.HomeScreen.route

    LaunchedEffect(Unit) {
        Log.d(
            "onTotalWaterTrackingResourceAmount Onboarding",
            onTotalWaterTrackingResourceAmount.toString()
        )
        getUpdateTotalWaterTrackingAmount(onTotalWaterTrackingResourceAmount)
        getGreeting()
        delay(3000)
        onTitleChage = true
        delay(3000)
        onTitleChage = false
    }


    Scaffold(
        modifier = modifier,
        topBar = {
            if (isOnHome) {
                TopBarLayout(
                    onTime = onTime,
                    onUserName = onUserName,
                    onTitleChange = onTitleChage,
                )
            }
        },
        bottomBar = {
            Log.d("SCROLL", showBottomBar.toString())
            AnimatedVisibility(visible = !showBottomBar, enter = fadeIn(), exit = fadeOut()) {
                BottomBarLayout(navController, navScreens = navItems, getHome = {
                    onHome = it
                })
            }

        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !showBottomBar && isOnHome,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        onWaterAddSheet = !onWaterAddSheet
                    },
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
                    AnimatedVisibility(visible = !onAdd, enter = fadeIn(), exit = fadeOut()) {
                        Box(
                            modifier = Modifier
                                .background(color = waterColor, shape = RoundedCornerShape(16.dp))
                                .size(width = 100.dp, height = 60.dp)
                        ) {
                            Text(
                                text = "Add", modifier = Modifier
                                    .align(
                                        Alignment.Center
                                    ),
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
            }


        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        val padding = it
        NavHost(
            navController = navController,
            startDestination = BottomNavScreens.HomeScreen.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {


            composable(route = BottomNavScreens.HomeScreen.route) {
                HomeScreen(
                    onPad = padding,
                    onWaterTrackingResourceAmount = onWaterTrackingResourceAmount,
                    onTotalWaterTrackingResourceAmount = onTotalWaterTrackingResourceAmount,
                    getReward = getReward,
                    onReward = onReward,
                    getBottomBar = {
                        showBottomBar = it
                    },
                    onWaterMeterResourceAmount = onWaterMeterResourceAmount,
                    onProgress = onProgress,
                    onStreak = onStreak,
                    streakImages = streakImages,
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
            composable(route = BottomNavScreens.SettingsScreen.route) {
                SettingsScreen(
                    profileData = profileData(
                        onNotificationChange = notificationsEnabled,
                        selectedIntervalIndex = selectedIntervalIndex,
                        wakeUpHour = wakeUpHour,
                        wakeUpMinute = wakeUpMinute,
                        bedHour = bedHour,
                        bedMinute = bedMinute,
                        selectedSoundIndex = selectedSoundIndex
                    ),
                    getNotificationChange = {
                        notificationsEnabled = it
                        prefs.edit { putBoolean("notifications_enabled", it) }
                        rescheduleNotifications()
                    },
                    getIntervalChange = {
                        selectedIntervalIndex = it
                        prefs.edit { putInt("notification_interval_index", it) }
                        rescheduleNotifications()
                    },
                    getWakeUpHourChange = { h, m ->
                        wakeUpHour = h
                        wakeUpMinute = m
                        prefs.edit { putInt("wake_up_hour", h).putInt("wake_up_minute", m) }
                        rescheduleNotifications()
                    },
                    getBedHourChange = { h, m ->
                        bedHour = h
                        bedMinute = m
                        prefs.edit { putInt("bed_hour", h).putInt("bed_minute", m) }
                        rescheduleNotifications()
                    },
                    getSoundChange = {
                        selectedSoundIndex = it
                        prefs.edit { putInt("notification_sound_index", it) }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                start = Offset(Float.POSITIVE_INFINITY, 0f),
                                end = Offset(0f, Float.POSITIVE_INFINITY),
                                colors = mutableListOf(backgroundColor1, backgroundColor2)
                            )
                        )
                        .padding(
                            bottom = padding.calculateBottomPadding() / 4,
                            top = padding.calculateTopPadding() / 8
                        )
                )
            }
        }
    }
    if (onWaterAddSheet) {
        Dialog(properties = DialogProperties(
            usePlatformDefaultWidth = true,
            decorFitsSystemWindows = true
        ),
            onDismissRequest = { onWaterAddSheet = !onWaterAddSheet }) {
            WaterCarouselSheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ),
                listState,
                isEndless,
                items,
                selected,
                getWaterTrackingResourceAmount,
                getSelected = {
                    selected = it
                },
                getWaterAddSheet = {
                    onWaterAddSheet = it
                })

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
    val visibleItems = 3 // number of visible items in the picker
    val itemHeightDp = 56.dp
    val pickerHeight = itemHeightDp * visibleItems

    val totalDataItems = if (isEndless) Int.MAX_VALUE else items.size

    // Snap to the center item when scrolling stops
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            // Data items are indices 1..totalDataItems (index 0 is top spacer, totalDataItems+1 is bottom spacer)
            val closestItem = layoutInfo.visibleItemsInfo
                .filter { it.index in 1..totalDataItems }
                .minByOrNull {
                    kotlin.math.abs((it.offset + it.size / 2) - viewportCenter)
                }
            closestItem?.let {
                val dataIndex = (it.index - 1) % items.size
                getSelected(dataIndex)
                listState.animateScrollToItem(it.index, -(layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset - it.size) / 2)
            }
        }
    }

    // Scroll to selected item on first composition
    LaunchedEffect(Unit) {
        if (items.isNotEmpty()) {
            // Add 1 for the top spacer item
            val startIndex = if (isEndless) {
                val midpoint = (Int.MAX_VALUE / 2)
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
        // Header
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
                text = if (selected >= 0 && selected < items.size) "${items[selected]} ml" else "",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(500),
                    color = waterColor,
                )
            )
        }

        // Picker wheel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pickerHeight)
        ) {
            // Selection highlight band
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
                // Top spacer so first item can be centered
                item {
                    Spacer(modifier = Modifier.height(itemHeightDp))
                }
                items(
                    count = if (isEndless) Int.MAX_VALUE else items.size,
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
                                    fontWeight = if (isSelected) FontWeight(600) else FontWeight(400),
                                    color = if (isSelected) primaryBlack else primaryBlack.copy(alpha = 0.35f),
                                    textAlign = TextAlign.Center,
                                )
                            )
                        }
                    }
                )
                // Bottom spacer so last item can be centered
                item {
                    Spacer(modifier = Modifier.height(itemHeightDp))
                }
            }

            // Top and bottom fade gradients
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
                if (selected >= 0 && selected < items.size) {
                    getWaterTrackingResourceAmount(items[selected])
                    getWaterAddSheet(false)
                    getSelected(0)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarLayout(
    onTime: String,
    onTitleChange: Boolean,
    onUserName: String,
) {

    TopAppBar(
        title = {
            AnimatedVisibility(
                visible = !onTitleChange, enter = fadeIn(), exit = fadeOut()
            ) {
                Text(
                    text = "$onTime \uD83D\uDC4B",
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontFamily = fontFamilyLight,
                        fontWeight = FontWeight(200),
                        color = primaryBlack,
                        textAlign = TextAlign.Start,
                    )
                )
            }
            AnimatedVisibility(
                visible = onTitleChange, enter = fadeIn(), exit = fadeOut()
            ) {
                Text(
                    text = "$onUserName \uD83D\uDC4B",
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontFamily = fontFamilyLight,
                        fontWeight = FontWeight(200),
                        color = primaryBlack,
                        textAlign = TextAlign.Start,
                    )
                )
            }

        },
        actions = {
//            Row(
//                modifier = Modifier,
//                horizontalArrangement = Arrangement.Start,
//                verticalAlignment = Alignment.Top
//            ) {
//                IconButton(onClick = { /*TODO*/ }) {
//                    Icon(
//                        imageVector = ImageVector.vectorResource(R.drawable.notification_icon),
//                        contentDescription = "notification",
//                        tint = minorColor
//                    )
//                }
//                IconButton(onClick = {getPorfileClick() }) {
//                    Image(
//                        modifier = imgModifier,
//                        painter = painterResource(id = R.drawable.hydrohabit_name_logo),
//                        contentDescription = "UserIcon"
//                    )
//                }
//            }

        },
        colors = TopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = Color.Transparent,
            titleContentColor = Color.Transparent,
            actionIconContentColor = Color.Transparent
        ), modifier = Modifier
            .padding(top = 8.dp, bottom = 0.dp, start = 8.dp, end = 16.dp)
            .clip(shape = RoundedCornerShape(8.dp))
    )
}

@Composable
fun BottomBarLayout(
    navController: NavHostController,
    navScreens: List<BottomNavScreens>,
    getHome: (Boolean) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState();
    val currentRoute = navBackStackEntry?.destination?.route
    BottomAppBar(
        modifier = Modifier
            .height(80.dp)
            .clip(
                shape = RoundedCornerShape(
                    topStart = 0.dp, topEnd = 0.dp
                )
            )
            .border(width = 0.5.dp, color = Color(0xFFD1D1D6), shape = RoundedCornerShape(0.dp)),
        containerColor = backgroundColor1
    ) {
        NavigationBar(containerColor = backgroundColor1) {
            navScreens.fastForEachIndexed { _, bottomData ->
                NavigationBarItem(
                    selected = bottomData.route == currentRoute,
                    onClick = {
                        navController.navigate(bottomData.route) {
                            when (bottomData.route) {
                                "Track" -> {
                                    getHome(false)
                                }
                                "Settings" -> {
                                    getHome(false)
                                }
                                else -> {
                                    getHome(true)
                                }
                            }
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
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
                            Log.d("Navigation", bottomData.route)
                            Icon(
                                imageVector = ImageVector.vectorResource(bottomData.icon),
                                contentDescription = bottomData.route,
                                tint = if (currentRoute == bottomData.route) waterColor else Color(0xFF8E8E93),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AnimatedVisibility(
                                visible = bottomData.route == currentRoute,
                                enter = expandHorizontally(),
                                exit = shrinkHorizontally()
                            ) {
                                Text(
                                    text = bottomData.route,
                                    modifier = Modifier,
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = fontFamilyLight,
                                        fontWeight = FontWeight(200),
                                        color = waterColor,
                                        textAlign = TextAlign.Start,
                                    )
                                )
                            }


                        }

                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        waterColor, indicatorColor = waterColor.copy(alpha = 0.12f)
                    )
                )
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBottomBarHostingScreen() {
    val navController = rememberNavController()

    BottomBarHostingScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    start = Offset(Float.POSITIVE_INFINITY, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                    colors = mutableListOf(backgroundColor1, backgroundColor2)
                )
            ),
        navController = navController,
        onReward = false,
        onTotalWaterTrackingResourceAmount = 300,
        onUserName = "Hitesh",
        onWaterTrackingResourceAmount = 300,
        getReward = {},
        getWaterTrackingResourceAmount = {},
        onWaterMeterResourceAmount = 20,
        onStreak = "6",
        onProgress = "You are half way through keep it going",
        onTime = "Goodmorning",
        getGreeting = {},
        items = mutableListOf(50, 100, 200, 300, 400, 500),
        getUpdateTotalWaterTrackingAmount = {},
        streakImages = mutableListOf(R.drawable.day2, R.drawable.day1),
    )
}