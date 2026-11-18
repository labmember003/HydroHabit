package com.falcon.hydrohabit.settings

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

private const val TAG = "PlatformActions.ios"

private const val BUG_REPORT_EMAIL = "falcontechlab@gmail.com"
private const val BUG_REPORT_SUBJECT = "Droply - Bug Report"

private fun openMailScreen(emailId: String, titleMessage: String?) {
    // Percent-encode spaces so NSURL.URLWithString stays valid (raw spaces make it return nil).
    val mailtoUrlString = if (titleMessage != null) {
        "mailto:$emailId?subject=${titleMessage.replace(" ", "%20")}"
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
