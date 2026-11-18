package com.falcon.hydrohabit.features.history.source

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import com.falcon.hydrohabit.features.history.model.IntakeEntry
import com.falcon.hydrohabit.features.history.model.IntakeLog
import com.falcon.hydrohabit.model.storage_utils.dataStorePath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path

/** Stores every logged drink in `intake_log.json`, next to the app's other stores. */
class IntakeLogRepository(storePath: () -> Path) {

    constructor(context: Any?) : this({ dataStorePath(context, "intake_log.json") })

    private val store: DataStore<IntakeLog> = DataStoreFactory.create(
        storage = OkioStorage(FileSystem.SYSTEM, IntakeLogSerializer, producePath = storePath)
    )

    val log: Flow<IntakeLog> = store.data

    suspend fun current(): IntakeLog = store.data.first()

    /** Adds [entry] (or re-adds a removed one) and remembers [goalMl] as that day's goal. */
    suspend fun add(entry: IntakeEntry, goalMl: Int) {
        store.updateData { log ->
            val entries = (log.entries.filterNot { it.id == entry.id } + entry)
                .sortedBy { it.epochMillis }
                .takeLast(MAX_ENTRIES)
            val goals = if (goalMl > 0) log.goals + (entry.date to goalMl) else log.goals
            log.copy(entries = entries, goals = goals)
        }
    }

    /** Removes the entry with [id] and returns it, or null if it no longer exists. */
    suspend fun remove(id: String): IntakeEntry? {
        var removed: IntakeEntry? = null
        store.updateData { log ->
            removed = log.entries.firstOrNull { it.id == id }
            if (removed == null) log else log.copy(entries = log.entries.filterNot { it.id == id })
        }
        return removed
    }

    suspend fun setHealthId(id: String, healthId: String?) {
        store.updateData { log ->
            log.copy(entries = log.entries.map { if (it.id == id) it.copy(healthId = healthId) else it })
        }
    }

    suspend fun recordGoal(date: String, goalMl: Int) {
        if (goalMl <= 0) return
        store.updateData { log ->
            if (log.goals[date] == goalMl) log else log.copy(goals = log.goals + (date to goalMl))
        }
    }

    private companion object {
        // Roughly ten years of heavy use; keeps the file small enough to load instantly.
        const val MAX_ENTRIES = 30_000
    }
}

private object IntakeLogSerializer : OkioSerializer<IntakeLog> {
    private val json = Json { ignoreUnknownKeys = true }

    override val defaultValue: IntakeLog = IntakeLog()

    override suspend fun readFrom(source: BufferedSource): IntakeLog = try {
        json.decodeFromString(IntakeLog.serializer(), source.readByteArray().decodeToString())
    } catch (e: SerializationException) {
        defaultValue
    }

    override suspend fun writeTo(t: IntakeLog, sink: BufferedSink) {
        sink.write(json.encodeToString(IntakeLog.serializer(), t).encodeToByteArray())
    }
}
