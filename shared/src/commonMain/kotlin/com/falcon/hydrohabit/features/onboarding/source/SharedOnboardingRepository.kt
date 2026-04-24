package com.falcon.hydrohabit.features.onboarding.source

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.falcon.hydrohabit.features.homescreen.utils.StreakClass
import com.falcon.hydrohabit.features.homescreen.utils.StreakMonthClass
import com.falcon.hydrohabit.features.homescreen.utils.UserSettings
import com.falcon.hydrohabit.features.homescreen.utils.UserValues
import com.falcon.hydrohabit.features.homescreen.utils.WaterAmount
import com.falcon.hydrohabit.model.storage_utils.OkioSerializerStreak
import com.falcon.hydrohabit.model.storage_utils.OkioSerializerStreakMonth
import com.falcon.hydrohabit.model.storage_utils.OkioSerializerUserSetting
import com.falcon.hydrohabit.model.storage_utils.OkioSerializerUserValues
import com.falcon.hydrohabit.model.storage_utils.OkioSerializerWaterAmount
import com.falcon.hydrohabit.model.storage_utils.dataStorePath
import kotlinx.coroutines.flow.Flow
import okio.FileSystem
import okio.SYSTEM

/**
 * KMM-compatible OnboardingRepository implementation using multiplatform DataStore (okio).
 *
 * IMPORTANT: File names are identical to the legacy Android DataStore files
 * to ensure seamless data migration for existing users.
 *
 * @param context platform context (Android Context on Android, null on iOS)
 */
class SharedOnboardingRepository(private val context: Any?) : OnboardingRepositoryContract {

    private val streakStore: DataStore<StreakClass> = DataStoreFactory.create(
        storage = OkioStorage(FileSystem.SYSTEM, OkioSerializerStreak) {
            dataStorePath(context, "streak_store.json")
        }
    )

    private val streakStoreMonth: DataStore<StreakMonthClass> = DataStoreFactory.create(
        storage = OkioStorage(FileSystem.SYSTEM, OkioSerializerStreakMonth) {
            dataStorePath(context, "streak_store_month.json")
        }
    )

    private val userSettingsStore: DataStore<UserSettings> = DataStoreFactory.create(
        storage = OkioStorage(FileSystem.SYSTEM, OkioSerializerUserSetting) {
            dataStorePath(context, "user_settings_store.json")
        }
    )

    private val waterAmountStore: DataStore<WaterAmount> = DataStoreFactory.create(
        storage = OkioStorage(FileSystem.SYSTEM, OkioSerializerWaterAmount) {
            dataStorePath(context, "water_amount.json")
        }
    )

    private val userValuesStore: DataStore<UserValues> = DataStoreFactory.create(
        storage = OkioStorage(FileSystem.SYSTEM, OkioSerializerUserValues) {
            dataStorePath(context, "user_values.json")
        }
    )

    // --- User Values ---

    override suspend fun updateUserValues(avgIntake: String, bestStreak: String) {
        userValuesStore.updateData {
            it.copy(bestStreak = bestStreak, avgIntake = avgIntake)
        }
    }

    override fun getUserValues(): Flow<UserValues> = userValuesStore.data

    // --- Streak ---

    override suspend fun updateStreak(
        streak: Int, streakDays: List<Int>, streakDay: String, waterTime: String, perks: List<Int>
    ) {
        streakStore.updateData {
            it.copy(
                streak = streak,
                streakDays = streakDays,
                streakDay = streakDay,
                waterTime = waterTime,
                perks = perks
            )
        }
        println("streakScore Onboarding updateStreak $perks")
    }

    override fun getStreak(): Flow<StreakClass> = streakStore.data

    // --- Streak Month ---

    override suspend fun updateStreakMonth(streakDay: Int, streakMonth: Int, streakYear: Int) {
        streakStoreMonth.updateData {
            it.copy(
                streakDay = streakDay,
                streakMonth = streakMonth,
                streakYear = streakYear
            )
        }
        println("streakScore Onboarding updateStreakMonth $streakDay")
    }

    override fun getStreakMonth(): Flow<StreakMonthClass> = streakStoreMonth.data

    // --- User Settings ---

    override suspend fun updateUserSettingsStore(
        userHeight: Int, userWaterIntake: Int, userName: String, userWeight: Int, onBoardingCompleted: Boolean
    ) {
        userSettingsStore.updateData {
            it.copy(
                userHeight = userHeight,
                userName = userName,
                userWaterIntake = userWaterIntake,
                userWeight = userWeight,
                registrationCompleted = onBoardingCompleted
            )
        }
        println("streakScore Onboarding updateUserSettingsStore $userWaterIntake")
    }

    override fun getUserSettingsStore(): Flow<UserSettings> = userSettingsStore.data

    // --- Water Amount ---

    override suspend fun updateWaterAmount(onUsedWater: Int, onTotalWaterAmount: Int, onWaterDay: String) {
        waterAmountStore.updateData {
            it.copy(
                onUsedWater = onUsedWater,
                onTotalWater = onTotalWaterAmount,
                onWaterDay = onWaterDay
            )
        }
        println("streakScore Onboarding WaterAmount $onUsedWater")
    }

    override fun removeWaterAmount() {
        // No-op, same as original
    }

    override fun getWaterAmount(): Flow<WaterAmount> = waterAmountStore.data
}



