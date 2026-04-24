package com.falcon.hydrohabit.model.storage_utils

import okio.Path

/**
 * Platform-specific DataStore file path provider.
 *
 * On Android: resolves to context.filesDir/datastore/<fileName>
 *   (MUST match the path Android's dataStore delegate uses for data continuity)
 * On iOS: resolves to Documents/datastore/<fileName>
 *
 * @param context platform context — Android Context on Android, unused (null) on iOS
 * @param fileName the DataStore file name (e.g. "streak_store.json")
 */
expect fun dataStorePath(context: Any?, fileName: String): Path

