package com.falcon.hydrohabit.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

/**
 * Multiplatform JSON file storage.
 * Reads/writes serializable objects to JSON files on both platforms.
 */
expect class JsonFileStorage(fileName: String) {
    suspend fun readString(): String?
    suspend fun writeString(content: String)
}

// Convenience extensions
suspend inline fun <reified T> JsonFileStorage.read(default: T): T {
    val raw = readString() ?: return default
    return try {
        Json.decodeFromString<T>(raw)
    } catch (e: Exception) {
        default
    }
}

suspend inline fun <reified T> JsonFileStorage.write(value: T) {
    writeString(Json.encodeToString(kotlinx.serialization.serializer<T>(), value))
}

