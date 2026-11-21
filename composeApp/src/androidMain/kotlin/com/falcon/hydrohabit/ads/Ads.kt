package com.falcon.hydrohabit.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Consent first (Google UMP: shows the GDPR form only where the law needs it), then the Mobile Ads SDK.
 * Banners wait for [ready], so no ad is requested before consent is known.
 */
object Ads {
    private val started = AtomicBoolean(false)
    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready
    private val _privacyOptionsRequired = MutableStateFlow(false)
    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired

    fun gatherConsent(activity: Activity) {
        val info = UserMessagingPlatform.getConsentInformation(activity)
        info.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    refresh(activity, info)
                }
            },
            { refresh(activity, info) },   // offline etc.: fall back to the consent stored last time
        )
        refresh(activity, info)             // consent from an earlier session lets ads start right away
    }

    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            refresh(activity, UserMessagingPlatform.getConsentInformation(activity))
        }
    }

    private fun refresh(context: Context, info: ConsentInformation) {
        _privacyOptionsRequired.value =
            info.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        if (info.canRequestAds() && started.compareAndSet(false, true)) {
            val app = context.applicationContext
            CoroutineScope(Dispatchers.IO).launch {
                MobileAds.initialize(app) { _ready.value = true }
            }
        }
    }
}
