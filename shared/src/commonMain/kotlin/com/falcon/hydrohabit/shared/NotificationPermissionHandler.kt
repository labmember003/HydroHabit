package com.falcon.hydrohabit.shared

/**
 * Cross-platform contract for notification permission handling.
 * Android implements this with moko-permissions + Android permission APIs.
 * iOS will implement this with moko-permissions + iOS UNUserNotificationCenter.
 *
 * NOTE: This is a scaffolding interface only for now.
 * Existing Android permission UI screens remain untouched.
 * Will be wired into the app in a future phase.
 */
interface NotificationPermissionHandler {
    /**
     * Request notification permission from the user.
     * @return true if permission was granted, false otherwise
     */
    suspend fun requestNotificationPermission(): Boolean

    /**
     * Check if notification permission is currently granted.
     */
    suspend fun isNotificationPermissionGranted(): Boolean
}

