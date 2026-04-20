package com.falcon.hydrohabit.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
actual class JsonFileStorage actual constructor(private val fileName: String) {

    private fun getFilePath(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory, NSUserDomainMask, true
        )
        val documentsDir = paths.first() as String
        return "$documentsDir/$fileName"
    }

    actual suspend fun readString(): String? {
        val path = getFilePath()
        val fileManager = NSFileManager.defaultManager
        return if (fileManager.fileExistsAtPath(path)) {
            NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null)
        } else {
            null
        }
    }

    actual suspend fun writeString(content: String) {
        val path = getFilePath()
        (content as NSString).writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
    }
}
