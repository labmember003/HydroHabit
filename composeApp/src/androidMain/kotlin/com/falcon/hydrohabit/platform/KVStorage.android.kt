package com.falcon.hydrohabit.platform

import android.content.Context
import android.content.SharedPreferences

actual class KVStorage(private val prefs: SharedPreferences) {
    actual suspend fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getString(key: String, default: String): String {
        return prefs.getString(key, default) ?: default
    }

    actual suspend fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    actual fun getInt(key: String, default: Int): Int {
        return prefs.getInt(key, default)
    }

    actual suspend fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean {
        return prefs.getBoolean(key, default)
    }
}

