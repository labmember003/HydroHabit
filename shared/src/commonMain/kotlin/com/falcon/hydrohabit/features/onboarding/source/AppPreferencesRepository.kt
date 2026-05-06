package com.falcon.hydrohabit.features.onboarding.source

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.falcon.hydrohabit.model.storage_utils.OkioSerializerAppPreferences
import com.falcon.hydrohabit.model.storage_utils.dataStorePath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okio.FileSystem
import okio.SYSTEM

/**
 * KMM-compatible repository for app preferences (replaces Android SharedPreferences).
 *
 * Provides both Flow-based (reactive) and suspend (one-shot) access patterns.
 *
 * @param context platform context (Android Context on Android, null on iOS)
 */
class AppPreferencesRepository(private val context: Any?) {

    private val prefsStore: DataStore<AppPreferences> = DataStoreFactory.create(
        storage = OkioStorage(FileSystem.SYSTEM, OkioSerializerAppPreferences) {
            dataStorePath(context, "app_preferences.json")
        }
    )

    /** Observe all preferences as a Flow */
    val preferencesFlow: Flow<AppPreferences> = prefsStore.data

    /** Read current snapshot (suspend — use sparingly) */
    suspend fun current(): AppPreferences = prefsStore.data.first()

    // --- Individual update helpers ---

    suspend fun setOnboardingCompleted(completed: Boolean) {
        prefsStore.updateData { it.copy(onboardingCompleted = completed) }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        prefsStore.updateData { it.copy(notificationsEnabled = enabled) }
    }

    suspend fun setWakeUpTime(hour: Int, minute: Int) {
        prefsStore.updateData { it.copy(wakeUpHour = hour, wakeUpMinute = minute) }
    }

    suspend fun setBedTime(hour: Int, minute: Int) {
        prefsStore.updateData { it.copy(bedHour = hour, bedMinute = minute) }
    }

    suspend fun setNotificationIntervalIndex(index: Int) {
        prefsStore.updateData { it.copy(notificationIntervalIndex = index) }
    }

    suspend fun setNotificationSoundIndex(index: Int) {
        prefsStore.updateData { it.copy(notificationSoundIndex = index) }
    }

    suspend fun setCustomSoundUri(uri: String?) {
        prefsStore.updateData { it.copy(customSoundUri = uri) }
    }

    /** Bulk update (for migration or settings screen) */
    suspend fun update(transform: (AppPreferences) -> AppPreferences) {
        prefsStore.updateData(transform)
    }
}

