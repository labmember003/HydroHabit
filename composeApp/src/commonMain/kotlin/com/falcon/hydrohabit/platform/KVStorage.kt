package com.falcon.hydrohabit.platform

import kotlinx.coroutines.flow.Flow

/**
 * Multiplatform key-value storage abstraction.
 * Backed by DataStore on Android, NSUserDefaults on iOS.
 */
expect class KVStorage {
    suspend fun putString(key: String, value: String)
    fun getString(key: String, default: String = ""): String
    suspend fun putInt(key: String, value: Int)
    fun getInt(key: String, default: Int = 0): Int
    suspend fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, default: Boolean = false): Boolean
}

