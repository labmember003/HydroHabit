package com.falcon.hydrohabit.platform

import android.content.Context
import java.io.File

actual class JsonFileStorage actual constructor(private val fileName: String) {
    private var context: Context? = null

    constructor(fileName: String, context: Context) : this(fileName) {
        this.context = context
    }

    companion object {
        private var appContext: Context? = null
        fun init(context: Context) {
            appContext = context.applicationContext
        }
    }

    private fun getFile(): File {
        val ctx = context ?: appContext ?: throw IllegalStateException("JsonFileStorage not initialized. Call JsonFileStorage.init(context) first.")
        return File(ctx.filesDir, fileName)
    }

    actual suspend fun readString(): String? {
        val file = getFile()
        return if (file.exists()) file.readText() else null
    }

    actual suspend fun writeString(content: String) {
        getFile().writeText(content)
    }
}

