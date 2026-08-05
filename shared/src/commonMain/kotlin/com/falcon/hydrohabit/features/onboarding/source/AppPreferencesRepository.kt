package com.falcon.hydrohabit.features.onboarding.source

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.falcon.hydrohabit.features.onboarding.usecase.Gender
import com.falcon.hydrohabit.model.storage_utils.OkioSerializerAppPreferences
import com.falcon.hydrohabit.model.storage_utils.dataStorePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import okio.FileSystem
import okio.SYSTEM

/**
 * KMM-compatible repository for app preferences (replaces Android SharedPreferences).
 *
 * The [cachedPreferences] StateFlow is started EAGERLY at construction time,
 * so the data is warmed up by the time any UI reads it — no flicker on launch.
 *
 * @param context platform context (Android Context on Android, null on iOS)
 */
class AppPreferencesRepository(private val context: Any?) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val prefsStore: DataStore<AppPreferences> = DataStoreFactory.create(
        storage = OkioStorage(FileSystem.SYSTEM, OkioSerializerAppPreferences) {
            dataStorePath(context, "app_preferences.json")
        }
    )

    /** Observe all preferences as a Flow */
    val preferencesFlow: Flow<AppPreferences> = prefsStore.data

    /**
     * Eagerly-cached StateFlow — starts reading from disk immediately on creation.
     * By the time the first composable frame renders, .value is already the real data.
     */
    val cachedPreferences: StateFlow<AppPreferences> = prefsStore.data
        .stateIn(scope, SharingStarted.Eagerly, AppPreferences())

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

    suspend fun setWeightUnit(unit: String) {
        prefsStore.updateData { it.copy(weightUnit = unit) }
    }

    suspend fun setActivityLevel(level: Int) {
        prefsStore.updateData { it.copy(activityLevel = level) }
    }

    suspend fun setGender(gender: Gender) {
        prefsStore.updateData { it.copy(gender = gender.name) }
    }

    /** Bulk update (for migration or settings screen) */
    suspend fun update(transform: (AppPreferences) -> AppPreferences) {
        prefsStore.updateData(transform)
    }
}

