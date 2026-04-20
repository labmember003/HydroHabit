package com.falcon.hydrohabit.platform

import platform.Foundation.NSUserDefaults

actual class KVStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual suspend fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    actual fun getString(key: String, default: String): String {
        return defaults.stringForKey(key) ?: default
    }

    actual suspend fun putInt(key: String, value: Int) {
        defaults.setInteger(value.toLong(), forKey = key)
    }

    actual fun getInt(key: String, default: Int): Int {
        // NSUserDefaults returns 0 if key doesn't exist
        return if (defaults.objectForKey(key) != null) {
            defaults.integerForKey(key).toInt()
        } else {
            default
        }
    }

    actual suspend fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean {
        return if (defaults.objectForKey(key) != null) {
            defaults.boolForKey(key)
        } else {
            default
        }
    }
}

