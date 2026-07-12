package com.falcon.hydrohabit.composeapp.settings

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

private const val TAG = "PlatformActions.ios"

private const val BUG_REPORT_EMAIL = "falcontechlab@gmail.com"
// Percent-encoded so NSURL.URLWithString stays valid on iOS 15 (raw spaces make it return nil).
private const val BUG_REPORT_SUBJECT = "HydroHabit%20-%20Bug%20Report"

private fun openMailScreen(emailId: String, titleMessage: String?) {
    val mailtoUrlString = if (titleMessage != null) {
        "mailto:$emailId?subject=$titleMessage"
    } else {
        "mailto:$emailId"
    }
    println("$TAG: openMailScreen url=$mailtoUrlString")

    val mailtoUrl = NSURL.URLWithString(mailtoUrlString)
    if (mailtoUrl == null) {
        println("$TAG: ERROR could not build NSURL, falling back to share sheet")
        fallbackToShareSheet(emailId, titleMessage ?: "")
        return
    }

    val app = UIApplication.sharedApplication
    if (app.canOpenURL(mailtoUrl)) {
        println("$TAG: canOpenURL=true, opening mail app")
        app.openURL(
            mailtoUrl,
            options = mapOf<Any?, Any?>(),
            completionHandler = { success ->
                println("$TAG: openURL completion success=$success")
                if (!success) {
                    fallbackToShareSheet(emailId, titleMessage ?: "")
                }
            }
        )
    } else {
        println("$TAG: canOpenURL=false, falling back to share sheet")
        fallbackToShareSheet(emailId, titleMessage ?: "")
    }
}

private fun fallbackToShareSheet(emailId: String, titleMessage: String) {
    println("$TAG: presenting share sheet for $emailId")
    val shareText = if (titleMessage.isNotEmpty()) {
        "$titleMessage\n\nPlease send this to: $emailId"
    } else {
        "Please send this to: $emailId"
    }

    val activityViewController = UIActivityViewController(
        activityItems = listOf(shareText),
        applicationActivities = null
    )

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    if (rootViewController == null) {
        println("$TAG: ERROR no rootViewController to present share sheet")
        return
    }
    rootViewController.presentViewController(
        activityViewController,
        animated = true,
        completion = null
    )
}

@Composable
actual fun rememberPlatformActions(): PlatformActionsState {
    return PlatformActionsState(
        sendBugReport = {
            println("$TAG: sendBugReport tapped")
            openMailScreen(BUG_REPORT_EMAIL, BUG_REPORT_SUBJECT)
        }
    )
}
