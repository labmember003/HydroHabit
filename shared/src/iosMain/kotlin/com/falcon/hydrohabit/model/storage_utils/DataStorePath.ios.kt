package com.falcon.hydrohabit.model.storage_utils

import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSHomeDirectory

/**
 * iOS actual: stores DataStore files in the app's Documents directory.
 */
actual fun dataStorePath(context: Any?, fileName: String): Path {
    return (NSHomeDirectory() + "/Documents/datastore/$fileName").toPath()
}

