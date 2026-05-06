package com.falcon.hydrohabit.features.onboarding.presentation.permissionScreens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.utils.PermissionDeniedAlertDialog
import com.falcon.hydrohabit.features.onboarding.utils.SingleButton
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.onboardingBoxColor
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.primaryBlackLight
import com.falcon.hydrohabit.ui.theme.waterColorBackground
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun OnboardingNotifications(
    modifier: Modifier = Modifier,
    getAllow: () -> Unit,
    onPermissionDenied: Boolean,
    getPermissionDenied: (Boolean) -> Unit
) {
    var showPermissionDialog by remember {
        mutableStateOf(false)
    }
    var isPermanentlyDenied by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Inject repositories
    val appPrefsRepo: AppPreferencesRepository = koinInject()

    // Bind moko-permissions controller to Activity lifecycle
    val permissionsController: PermissionsController = koinInject()
    BindEffect(permissionsController)

    if (showPermissionDialog) {
        PermissionDeniedAlertDialog(
            getDismissButton = {
                showPermissionDialog = false
                getAllow()
                println("DISMISS BUTTON: Dismiss")
            },
            getConfirmButton = {
               val intent =  Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts(
                        "package",
                        context.packageName, null
                    )
                )
                context.startActivity(intent)
                println("CONFIRM BUTTON: Confirm")
            },
            getAppSettings = {
              val intent =   Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts(
                        "package",
                        context.packageName, null
                    )
                )
                context.startActivity(intent)
                println("GET TO APP SETTINGS: App Settings")
            },
            onPermissionTitle = "Notification Permission",
            onPermissionText = if (isPermanentlyDenied)
                "It seems you permanently denied Notification Permission. Notifications are necessary to remind of your water breaks. You can go into settings to grant it"
            else
                "Notification permission was denied. Notifications are necessary to remind of your water breaks. Please allow notifications.",
            onPermanentlyDenied = isPermanentlyDenied || onPermissionDenied,
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reminder to Drink",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    color = primaryBlackLight,
                    textAlign = TextAlign.Center,
                ), modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Every few hours we will remind you to drink water",
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

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(
                    onboardingBoxColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Notifications",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(500),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                ), modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "To remind you to drink water while you are away, we need permission to show notifications.",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = fontFamilyLight,
                    fontWeight = FontWeight(400),
                    color = primaryBlack,
                    textAlign = TextAlign.Center,
                ), modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            SingleButton(getNavigate = {
                scope.launch {
                    try {
                        permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
                        // Permission granted
                        println("Notification Permission: true (moko)")
                        getPermissionDenied(true)
                        appPrefsRepo.setNotificationsEnabled(true)
                        getAllow()
                    } catch (e: DeniedAlwaysException) {
                        // Permanently denied — show dialog with settings link
                        println("Notification Permission: permanently denied (moko)")
                        getPermissionDenied(false)
                        isPermanentlyDenied = true
                        showPermissionDialog = true
                    } catch (e: DeniedException) {
                        // Denied (but not permanently) — show dialog
                        println("Notification Permission: denied (moko)")
                        getPermissionDenied(false)
                        isPermanentlyDenied = false
                        showPermissionDialog = true
                    }
                }
            }, buttonName = "Allow")

        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewOnboardingNotificationsPermission() {
    OnboardingNotifications(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    start = Offset(Float.POSITIVE_INFINITY * 0.4f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                    colors = listOf(waterColorBackground, backgroundColor2)
                )
            ), getAllow = {}, onPermissionDenied = false, getPermissionDenied = {}
    )
}