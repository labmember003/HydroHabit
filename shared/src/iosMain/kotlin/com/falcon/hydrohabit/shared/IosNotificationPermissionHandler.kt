package com.falcon.hydrohabit.shared

/**
 * iOS stub implementation of NotificationPermissionHandler.
 * Will be properly implemented when iOS app development begins.
 */
class IosNotificationPermissionHandler : NotificationPermissionHandler {

    override suspend fun requestNotificationPermission(): Boolean {
        // TODO: Implement with moko-permissions for iOS
        println("NotificationPermission: iOS implementation pending")
        return false
    }

    override suspend fun isNotificationPermissionGranted(): Boolean {
        // TODO: Implement with moko-permissions for iOS
        return false
    }
}

