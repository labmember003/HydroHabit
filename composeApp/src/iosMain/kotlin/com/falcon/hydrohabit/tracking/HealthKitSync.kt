package com.falcon.hydrohabit.tracking

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.HealthKit.HKAuthorizationStatusSharingAuthorized
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKMetricPrefixMilli
import platform.HealthKit.HKQuantity
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifierDietaryWater
import platform.HealthKit.HKQuery
import platform.HealthKit.HKUnit
import platform.HealthKit.literUnitWithMetricPrefix
import platform.HealthKit.predicateForObjectWithUUID
import platform.UIKit.UIApplication
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** Saves logged drinks to Apple Health as "Water" (dietary water) samples. */
@OptIn(ExperimentalForeignApi::class)
class HealthKitSync : HealthSync {

    private val store by lazy { HKHealthStore() }

    private val waterType: HKQuantityType
        get() = HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryWater)!!

    override val isAvailable: Boolean
        get() = HKHealthStore.isHealthDataAvailable()

    override val providerName: String = "Apple Health"

    override suspend fun requestAccess(): Boolean {
        if (!isAvailable) return false
        return suspendCoroutine { cont ->
            store.requestAuthorizationToShareTypes(setOf(waterType), readTypes = null) { _, _ ->
                cont.resume(store.authorizationStatusForType(waterType) == HKAuthorizationStatusSharingAuthorized)
            }
        }
    }

    override suspend fun saveWater(amountMl: Int, epochMillis: Long): String? {
        if (!isAvailable || store.authorizationStatusForType(waterType) != HKAuthorizationStatusSharingAuthorized) return null
        val date = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
        val quantity = HKQuantity.quantityWithUnit(HKUnit.literUnitWithMetricPrefix(HKMetricPrefixMilli), amountMl.toDouble())
        val sample = HKQuantitySample.quantitySampleWithType(waterType, quantity, date, date)
        return suspendCoroutine { cont ->
            store.saveObject(sample) { success, _ ->
                cont.resume(if (success) sample.UUID.UUIDString else null)
            }
        }
    }

    override suspend fun deleteWater(healthId: String) {
        if (!isAvailable) return
        val uuid = NSUUID(uUIDString = healthId)
        val predicate = HKQuery.predicateForObjectWithUUID(uuid)
        suspendCoroutine { cont ->
            store.deleteObjectsOfType(waterType, predicate) { _, _, _ -> cont.resume(Unit) }
        }
    }

    override fun openHealthApp() {
        val url = NSURL.URLWithString("x-apple-health://") ?: return
        UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
    }
}
