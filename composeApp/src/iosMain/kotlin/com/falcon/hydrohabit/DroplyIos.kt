package com.falcon.hydrohabit

import com.falcon.hydrohabit.alarm.AlarmSchedulerContract
import com.falcon.hydrohabit.alarm.IosAlarmScheduler
import com.falcon.hydrohabit.di.appModule
import com.falcon.hydrohabit.features.onboarding.source.AppPreferencesRepository
import com.falcon.hydrohabit.features.onboarding.source.OnboardingRepositoryContract
import com.falcon.hydrohabit.shared.di.sharedModule
import com.falcon.hydrohabit.tracking.HealthKitSync
import com.falcon.hydrohabit.tracking.HealthSync
import com.falcon.hydrohabit.tracking.HydrationTracker
import kotlinx.coroutines.flow.first
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

/** Starts the shared app graph once, whether the UI or a Siri shortcut launches first. */
object DroplyIos {
    private var started = false

    fun start() {
        if (started) return
        started = true
        startKoin {
            modules(
                sharedModule(null),
                appModule(),
                module {
                    single<AlarmSchedulerContract> { IosAlarmScheduler() }
                    single<HealthSync> { HealthKitSync() }
                }
            )
        }
    }
}

/** What a Siri / Shortcuts action reports back to the user. */
class ShortcutResult(
    val loggedMl: Int,
    val totalMl: Int,
    val goalMl: Int,
    val reachedGoal: Boolean,
    /** False until onboarding is finished; the shortcut then asks the user to open the app. */
    val isSetUp: Boolean,
)

/** Entry points for the Swift App Intents (Siri, Shortcuts, Spotlight). */
object ShortcutBridge {

    private suspend fun isSetUp(): Boolean {
        DroplyIos.start()
        val koin = KoinPlatform.getKoin()
        return koin.get<AppPreferencesRepository>().current().onboardingCompleted &&
            koin.get<OnboardingRepositoryContract>().getUserSettingsStore().first().userWaterIntake > 0
    }

    @Throws(Throwable::class)
    suspend fun logWater(amountMl: Int): ShortcutResult {
        if (!isSetUp()) return ShortcutResult(0, 0, 0, reachedGoal = false, isSetUp = false)
        val change = KoinPlatform.getKoin().get<HydrationTracker>().addWater(amountMl.coerceIn(1, 5000))
        return ShortcutResult(amountMl, change.totalMl, change.goalMl, change.reachedGoal, isSetUp = true)
    }

    @Throws(Throwable::class)
    suspend fun today(): ShortcutResult {
        if (!isSetUp()) return ShortcutResult(0, 0, 0, reachedGoal = false, isSetUp = false)
        val koin = KoinPlatform.getKoin()
        koin.get<HydrationTracker>().reconcileToday()
        val water = koin.get<OnboardingRepositoryContract>().getWaterAmount().first()
        val today = com.falcon.hydrohabit.features.homescreen.usecase.currentTimeInfo().dateString
        val total = if (water.onWaterDay == today) water.onUsedWater else 0
        return ShortcutResult(0, total, water.onTotalWater, total >= water.onTotalWater && water.onTotalWater > 0, isSetUp = true)
    }
}
