package com.falcon.hydrohabit.model.storage_utils

import android.content.Context
import okio.Path
import okio.Path.Companion.toPath

/**
 * Android actual: resolves to the EXACT same path as Android's dataStore delegate:
 * <filesDir>/datastore/<fileName>
 *
 * This ensures existing user data is preserved seamlessly.
 */
actual fun dataStorePath(context: Any?, fileName: String): Path {
    val androidContext = context as Context
    return androidContext.filesDir.resolve("datastore").resolve(fileName).absolutePath.toPath()
}

