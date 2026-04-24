package com.falcon.hydrohabit.shared

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController

/**
 * Android implementation of NotificationPermissionHandler using moko-permissions.
 * Inject via DI with a PermissionsController bound to the Activity lifecycle.
 */
class AndroidNotificationPermissionHandler(
    private val permissionsController: PermissionsController
) : NotificationPermissionHandler {

    override suspend fun requestNotificationPermission(): Boolean {
        return try {
            permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
            true
        } catch (e: Exception) {
            println("NotificationPermission: denied or error — ${e.message}")
            false
        }
    }

    override suspend fun isNotificationPermissionGranted(): Boolean {
        return permissionsController.isPermissionGranted(Permission.REMOTE_NOTIFICATION)
    }
}

