package com.falcon.hydrohabit.utils

import com.falcon.hydrohabit.platform.logError

class Utils {
    companion object {
        var ApiKey = "iosdhgaoewrnfnodDFKFS234"

        fun String.capitalizeFirst(): String {
            if (isNotEmpty()) {
                return this.substring(0, 1).uppercase() + this.substring(1)
            }
            return this
        }

        fun logIt(tag: String, message: String) {
            logError(tag, message)
        }
    }
}
