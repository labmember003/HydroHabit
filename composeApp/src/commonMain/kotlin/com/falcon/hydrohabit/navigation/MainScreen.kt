package com.falcon.hydrohabit.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.alarm.AlarmSchedulerContract
import com.falcon.hydrohabit.ads.BannerAd
import com.falcon.hydrohabit.features.history.source.IntakeLogRepository
import com.falcon.hydrohabit.features.history.usecase.HistoryStats
import com.falcon.hydrohabit.features.homescreen.usecase.currentTimeInfo
import kotlinx.datetime.LocalDate
import com.falcon.hydrohabit.history.HistoryScreenHost
import com.falcon.hydrohabit.home.HomeScreenHost
import com.falcon.hydrohabit.tracking.HealthSync
import com.falcon.hydrohabit.tracking.HydrationTracker
import com.falcon.hydrohabit.ui.DroplySnackbarHost
import com.falcon.hydrohabit.ui.show
import com.falcon.hydrohabit.onboarding.components.WeightUnit
import com.falcon.hydrohabit.settings.SettingsScreen
import com.falcon.hydrohabit.settings.intervalMinutesMap
import com.falcon.hydrohabit.settings.rememberNotificationPermissionState
import com.falcon.hydrohabit.ui.theme.backgroundColor1
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.primaryBlackLight
import com.falcon.hydrohabit.ui.theme.waterColor
import com.falcon.hydrohabit.features.homescreen.utils.UserSettings
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import com.falcon.hydrohabit.features.onboarding.usecase.WaterIntakeCalculator
import com.falcon.hydrohabit.features.profilescreen.utils.ProfileData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private enum class Screen(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    History("History", Icons.Filled.DateRange),
    Settings("Settings", Icons.Filled.Settings),
}

@Composable
fun MainScreen(
    appPrefsRepo: AppPreferencesRepository,
    onboardingRepo: OnboardingRepositoryContract,
    alarmScheduler: AlarmSchedulerContract,
    tracker: HydrationTracker,
    intakeLogRepo: IntakeLogRepository,
    healthSync: HealthSync,
    shouldOpenAddWater: Boolean = false,
    onAddWaterHandled: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val showMessage: (String, String?, (() -> Unit)?) -> Unit = { message, action, onAction ->
        snackbarHostState.show(scope, message, action, onAction)
    }
    var showHealthDeniedDialog by remember { mutableStateOf(false) }

    // Water logged by older versions (before the intake log existed) gets one entry,
    // so History and the home screen agree.
    LaunchedEffect(Unit) { tracker.reconcileToday() }
    val appPrefs by appPrefsRepo.cachedPreferences.collectAsState()
    val userSettings by onboardingRepo.getUserSettingsStore()
        .collectAsState(initial = UserSettings())

    // The streak shown on Home: consecutive goal days from the intake log. Users who
    // upgraded from a version without the log keep their stored count until they log.
    val intakeLog by intakeLogRepo.log.collectAsState(initial = null)
    val logStreak = intakeLog?.takeIf { it.entries.isNotEmpty() }?.let { log ->
        HistoryStats.currentStreak(log, LocalDate.parse(currentTimeInfo().dateString), userSettings.userWaterIntake)
    }

    var currentScreen by remember { mutableStateOf(Screen.Home) }

    // Notification "Drink" action lands here — make sure the Home tab (which owns
    // the water picker dialog) is showing so the dialog can appear.
    LaunchedEffect(shouldOpenAddWater) {
        if (shouldOpenAddWater) {
            currentScreen = Screen.Home
        }
    }

    // Notification / settings state (hoisted here so the permission dialog and the
    // ON_RESUME re-check are always active while the app is running — matching the
    // app module's bottom-bar host, not just while the Settings tab is open).
    var notificationsEnabled by remember { mutableStateOf(appPrefs.notificationsEnabled) }
    var selectedIntervalIndex by remember { mutableIntStateOf(appPrefs.notificationIntervalIndex) }
    var wakeUpHour by remember { mutableIntStateOf(appPrefs.wakeUpHour) }
    var wakeUpMinute by remember { mutableIntStateOf(appPrefs.wakeUpMinute) }
    var bedHour by remember { mutableIntStateOf(appPrefs.bedHour) }
    var bedMinute by remember { mutableIntStateOf(appPrefs.bedMinute) }
    var selectedSoundIndex by remember { mutableIntStateOf(appPrefs.notificationSoundIndex) }
    var customSoundUri by remember { mutableStateOf(appPrefs.customSoundUri) }

    var showNotificationPermissionDialog by remember { mutableStateOf(false) }
    var pendingNotificationToggle by remember { mutableStateOf(false) }

    fun rescheduleNotifications() {
        if (notificationsEnabled) {
            val safeIndex = selectedIntervalIndex.coerceIn(intervalMinutesMap.indices)
            alarmScheduler.scheduleRepeating(
                intervalMinutesMap[safeIndex], wakeUpHour, wakeUpMinute, bedHour, bedMinute, selectedSoundIndex
            )
        } else {
            alarmScheduler.cancelAll()
        }
    }

    val permissionState = rememberNotificationPermissionState(
        onPermissionResult = { granted ->
            if (granted) {
                notificationsEnabled = true
                scope.launch { appPrefsRepo.setNotificationsEnabled(true) }
                rescheduleNotifications()
            } else {
                // Denied (or permanently denied) — guide user to app settings and revert.
                // Only reached when the user actively tries to enable notifications.
                showNotificationPermissionDialog = true
                if (pendingNotificationToggle) {
                    notificationsEnabled = false
                    scope.launch { appPrefsRepo.setNotificationsEnabled(false) }
                    pendingNotificationToggle = false
                }
            }
        }
    )

    LaunchedEffect(appPrefs) {
        notificationsEnabled = appPrefs.notificationsEnabled
        selectedIntervalIndex = appPrefs.notificationIntervalIndex
        wakeUpHour = appPrefs.wakeUpHour
        wakeUpMinute = appPrefs.wakeUpMinute
        bedHour = appPrefs.bedHour
        bedMinute = appPrefs.bedMinute
        selectedSoundIndex = appPrefs.notificationSoundIndex
        customSoundUri = appPrefs.customSoundUri
        rescheduleNotifications()
    }

    Scaffold(
        snackbarHost = {
            // Home has its own scaffold so the snackbar lifts the Add button instead of covering it.
            if (currentScreen != Screen.Home) DroplySnackbarHost(snackbarHostState)
        },
        bottomBar = {
            Column {
                // Banner sits just above the tab bar on every tab; it takes no space until an ad loads.
                BannerAd(modifier = Modifier.background(backgroundColor1))
                // iOS-style single hairline on top of the tab bar (not a full border box).
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(Color(0xFFD1D1D6))
                )
                // Android keeps the flat non-edge-to-edge tab bar (zero insets); iOS uses
                // the navigationBars inset so the bar clears the home-indicator strip
                // instead of the tabs sitting right on top of it.
                NavigationBar(
                    containerColor = backgroundColor1,
                    windowInsets = bottomBarWindowInsets()
                ) {
                    Screen.entries.forEach { screen ->
                        val selected = screen == currentScreen
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentScreen = screen },
                            icon = {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.label,
                                        tint = if (selected) waterColor else Color(0xFF8E8E93),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AnimatedVisibility(
                                        visible = selected,
                                        enter = expandHorizontally(),
                                        exit = shrinkHorizontally()
                                    ) {
                                        Text(
                                            text = screen.label,
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
                                selectedIconColor = waterColor,
                                indicatorColor = waterColor.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {
            when (currentScreen) {
                Screen.Home -> {
                    HomeScreenHost(
                        onboardingRepo = onboardingRepo,
                        alarmScheduler = alarmScheduler,
                        tracker = tracker,
                        notificationsEnabled = notificationsEnabled,
                        onShowMessage = showMessage,
                        snackbarHostState = snackbarHostState,
                        streakOverride = logStreak,
                        shouldOpenAddWater = shouldOpenAddWater,
                        onAddWaterHandled = onAddWaterHandled
                    )
                }
                Screen.History -> {
                    HistoryScreenHost(
                        intakeLogRepo = intakeLogRepo,
                        tracker = tracker,
                        currentGoalMl = userSettings.userWaterIntake,
                        onShowMessage = showMessage,
                    )
                }
                Screen.Settings -> {
                    SettingsScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    start = Offset(Float.POSITIVE_INFINITY, 0f),
                                    end = Offset(0f, Float.POSITIVE_INFINITY),
                                    colors = listOf(backgroundColor1, backgroundColor2)
                                )
                            )
                            .padding(top = 16.dp),
                        profileData = ProfileData(
                            onNotificationChange = notificationsEnabled,
                            selectedIntervalIndex = selectedIntervalIndex,
                            wakeUpHour = wakeUpHour,
                            wakeUpMinute = wakeUpMinute,
                            bedHour = bedHour,
                            bedMinute = bedMinute,
                            selectedSoundIndex = selectedSoundIndex,
                            customSoundUri = customSoundUri
                        ),
                        healthProviderName = healthSync.providerName.takeIf { healthSync.isAvailable },
                        healthSyncEnabled = appPrefs.healthSyncEnabled,
                        onHealthSyncChange = { enabled ->
                            scope.launch {
                                if (!enabled) {
                                    appPrefsRepo.setHealthSyncEnabled(false)
                                    showMessage("New drinks won't be saved to ${healthSync.providerName}", null, null)
                                } else if (healthSync.requestAccess()) {
                                    appPrefsRepo.setHealthSyncEnabled(true)
                                    tracker.syncTodayToHealth()
                                    showMessage("Drinks will be saved to ${healthSync.providerName}", null, null)
                                } else {
                                    showHealthDeniedDialog = true
                                }
                            }
                        },
                        heightCm = userSettings.userHeight,
                        weightKg = userSettings.userWeight,
                        gender = appPrefs.userGender,
                        initialWeightUnit = runCatching { WeightUnit.valueOf(appPrefs.weightUnit) }
                            .getOrDefault(WeightUnit.KG),
                        getWeightUnitChange = { scope.launch { appPrefsRepo.setWeightUnit(it.name) } },
                        getGenderChange = { newGender ->
                            scope.launch {
                                appPrefsRepo.setGender(newGender)
                                val newIntake = WaterIntakeCalculator.calculateWaterIntake(
                                    userSettings.userHeight, userSettings.userWeight, appPrefs.activityLevel, newGender
                                )
                                onboardingRepo.updateUserSettingsStore(
                                    userHeight = userSettings.userHeight,
                                    userWaterIntake = newIntake,
                                    userName = userSettings.userName,
                                    userWeight = userSettings.userWeight,
                                    onBoardingCompleted = true
                                )
                                val current = onboardingRepo.getWaterAmount().first()
                                onboardingRepo.updateWaterAmount(
                                    current.onUsedWater, newIntake, current.onWaterDay
                                )
                            }
                        },
                        getNotificationChange = { enabled ->
                            if (enabled) {
                                if (permissionState.hasPermission) {
                                    notificationsEnabled = true
                                    scope.launch { appPrefsRepo.setNotificationsEnabled(true) }
                                    rescheduleNotifications()
                                } else {
                                    pendingNotificationToggle = true
                                    permissionState.requestPermission()
                                }
                            } else {
                                notificationsEnabled = false
                                scope.launch { appPrefsRepo.setNotificationsEnabled(false) }
                                rescheduleNotifications()
                            }
                        },
                        getIntervalChange = {
                            selectedIntervalIndex = it
                            scope.launch { appPrefsRepo.setNotificationIntervalIndex(it) }
                            rescheduleNotifications()
                        },
                        getWakeUpHourChange = { h, m ->
                            wakeUpHour = h
                            wakeUpMinute = m
                            scope.launch { appPrefsRepo.setWakeUpTime(h, m) }
                            rescheduleNotifications()
                        },
                        getBedHourChange = { h, m ->
                            bedHour = h
                            bedMinute = m
                            scope.launch { appPrefsRepo.setBedTime(h, m) }
                            rescheduleNotifications()
                        },
                        getSoundChange = {
                            selectedSoundIndex = it
                            scope.launch { appPrefsRepo.setNotificationSoundIndex(it) }
                        },
                        onCustomSoundPicked = { uri ->
                            customSoundUri = uri
                            scope.launch { appPrefsRepo.setCustomSoundUri(uri) }
                        },
                        getHeightChange = { newHeight ->
                            scope.launch {
                                val newIntake = WaterIntakeCalculator.calculateWaterIntake(
                                    newHeight, userSettings.userWeight, appPrefs.activityLevel, appPrefs.userGender
                                )
                                onboardingRepo.updateUserSettingsStore(
                                    userHeight = newHeight,
                                    userWaterIntake = newIntake,
                                    userName = userSettings.userName,
                                    userWeight = userSettings.userWeight,
                                    onBoardingCompleted = true
                                )
                                val current = onboardingRepo.getWaterAmount().first()
                                onboardingRepo.updateWaterAmount(
                                    current.onUsedWater, newIntake, current.onWaterDay
                                )
                            }
                        },
                        getWeightChange = { newWeight ->
                            scope.launch {
                                val newIntake = WaterIntakeCalculator.calculateWaterIntake(
                                    userSettings.userHeight, newWeight, appPrefs.activityLevel, appPrefs.userGender
                                )
                                onboardingRepo.updateUserSettingsStore(
                                    userHeight = userSettings.userHeight,
                                    userWaterIntake = newIntake,
                                    userName = userSettings.userName,
                                    userWeight = newWeight,
                                    onBoardingCompleted = true
                                )
                                val current = onboardingRepo.getWaterAmount().first()
                                onboardingRepo.updateWaterAmount(
                                    current.onUsedWater, newIntake, current.onWaterDay
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    if (showHealthDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showHealthDeniedDialog = false },
            containerColor = backgroundColor1,
            titleContentColor = primaryBlack,
            textContentColor = primaryBlackLight,
            title = { Text("Allow Droply in ${healthSync.providerName}") },
            text = {
                Text(
                    "Droply doesn't have permission to save water yet. In the Health app, tap your " +
                        "profile picture, then Apps, Droply, and turn on Water. Then come back and switch this on again."
                )
            },
            confirmButton = {
                Button(onClick = {
                    showHealthDeniedDialog = false
                    healthSync.openHealthApp()
                }) { Text("Open Health") }
            },
            dismissButton = {
                TextButton(onClick = { showHealthDeniedDialog = false }) { Text("Not now") }
            }
        )
    }

    if (showNotificationPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationPermissionDialog = false },
            containerColor = backgroundColor1,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint = waterColor
                )
            },
            titleContentColor = primaryBlack,
            textContentColor = primaryBlackLight,
            title = { Text("Notification Permission Required") },
            text = { Text("Notifications are essential for reminding you to drink water. Please enable notification permission in app settings.") },
            confirmButton = {
                Button(onClick = {
                    showNotificationPermissionDialog = false
                    permissionState.openAppSettings()
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationPermissionDialog = false }) {
                    Text("Later")
                }
            }
        )
    }
}
