package com.falcon.hydrohabit.model.storage_utils

import androidx.datastore.core.okio.OkioSerializer
import com.falcon.hydrohabit.features.homescreen.utils.StreakClass
import com.falcon.hydrohabit.features.homescreen.utils.StreakMonthClass
import com.falcon.hydrohabit.features.homescreen.utils.UserSettings
import com.falcon.hydrohabit.features.homescreen.utils.UserValues
import com.falcon.hydrohabit.features.homescreen.utils.WaterAmount
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource

/**
 * Multiplatform OkioSerializer implementations for DataStore.
 * These replace the Android-only Serializer<T> implementations.
 * The JSON format is IDENTICAL to the existing serializers — full data compatibility.
 */

object OkioSerializerStreak : OkioSerializer<StreakClass> {
    override val defaultValue: StreakClass = StreakClass()

    override suspend fun readFrom(source: BufferedSource): StreakClass {
        return try {
            Json.decodeFromString(
                deserializer = StreakClass.serializer(),
                string = source.readByteArray().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: StreakClass, sink: BufferedSink) {
        sink.write(
            Json.encodeToString(
                serializer = StreakClass.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}

object OkioSerializerStreakMonth : OkioSerializer<StreakMonthClass> {
    override val defaultValue: StreakMonthClass = StreakMonthClass()

    override suspend fun readFrom(source: BufferedSource): StreakMonthClass {
        return try {
            Json.decodeFromString(
                deserializer = StreakMonthClass.serializer(),
                string = source.readByteArray().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: StreakMonthClass, sink: BufferedSink) {
        sink.write(
            Json.encodeToString(
                serializer = StreakMonthClass.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}

object OkioSerializerUserSetting : OkioSerializer<UserSettings> {
    override val defaultValue: UserSettings = UserSettings()

    override suspend fun readFrom(source: BufferedSource): UserSettings {
        return try {
            Json.decodeFromString(
                deserializer = UserSettings.serializer(),
                string = source.readByteArray().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserSettings, sink: BufferedSink) {
        sink.write(
            Json.encodeToString(
                serializer = UserSettings.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}

object OkioSerializerWaterAmount : OkioSerializer<WaterAmount> {
    override val defaultValue: WaterAmount = WaterAmount()

    override suspend fun readFrom(source: BufferedSource): WaterAmount {
        return try {
            Json.decodeFromString(
                deserializer = WaterAmount.serializer(),
                string = source.readByteArray().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: WaterAmount, sink: BufferedSink) {
        sink.write(
            Json.encodeToString(
                serializer = WaterAmount.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}

object OkioSerializerUserValues : OkioSerializer<UserValues> {
    override val defaultValue: UserValues = UserValues()

    override suspend fun readFrom(source: BufferedSource): UserValues {
        return try {
            Json.decodeFromString(
                deserializer = UserValues.serializer(),
                string = source.readByteArray().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserValues, sink: BufferedSink) {
        sink.write(
            Json.encodeToString(
                serializer = UserValues.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}

