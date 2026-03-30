package com.app.dialer.core.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.app.dialer.domain.model.SimCard
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper that interfaces with [SubscriptionManager] to retrieve SIM card information.
 *
 * All public methods catch [SecurityException] and any other runtime exceptions so
 * that callers receive a safe empty result rather than a crash when permissions are
 * absent or the device is in an unusual state (e.g. airplane mode, dual-SIM variants
 * that behave non-standardly).
 *
 * Requires [android.Manifest.permission.READ_PHONE_STATE] at runtime. If the
 * permission is not granted, all methods return empty / null results and log a
 * warning rather than throwing.
 */
@Singleton
class SimCardHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val subscriptionManager: SubscriptionManager =
        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

    companion object {
        private const val TAG = "SimCardHelper"
    }

    /**
     * Returns all currently active SIM card subscriptions as [SimCard] domain objects.
     *
     * - Requires [android.Manifest.permission.READ_PHONE_STATE].
     * - Returns an empty list in airplane mode, when no SIM is inserted, or when
     *   the permission is denied.
     * - [SimCard.isDefault] is true for the subscription whose ID matches
     *   [SubscriptionManager.getDefaultVoiceSubscriptionId].
     */
    fun getActiveSimCards(): List<SimCard> {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "READ_PHONE_STATE not granted; returning empty SIM list")
            return emptyList()
        }

        return try {
            val defaultVoiceSubId = SubscriptionManager.getDefaultVoiceSubscriptionId()
            subscriptionManager.activeSubscriptionInfoList?.map { info ->
                SimCard(
                    slotIndex = info.simSlotIndex,
                    subscriptionId = info.subscriptionId,
                    displayName = info.displayName?.toString()
                        ?: "SIM ${info.simSlotIndex + 1}",
                    carrierName = info.carrierName?.toString() ?: "",
                    isDefault = info.subscriptionId == defaultVoiceSubId,
                    isActive = true
                )
            } ?: emptyList()
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException reading active subscriptions", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read active SIM cards", e)
            emptyList()
        }
    }

    /**
     * Returns the [SimCard] that is the default for outgoing voice calls,
     * or null when no default is set or no SIM is available.
     */
    fun getDefaultVoiceSimCard(): SimCard? {
        val defaultSubId = SubscriptionManager.getDefaultVoiceSubscriptionId()
        return getActiveSimCards().firstOrNull { it.subscriptionId == defaultSubId }
    }

    /**
     * Returns the subscription ID for the SIM in the given physical slot index,
     * or null when the slot is empty, the permission is missing, or an error occurs.
     *
     * @param slotIndex 0-based hardware SIM slot index.
     */
    fun getSubscriptionIdForSlot(slotIndex: Int): Int? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "READ_PHONE_STATE not granted; cannot query slot $slotIndex")
            return null
        }

        return try {
            subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotIndex)
                ?.subscriptionId
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get subscriptionId for slot $slotIndex", e)
            null
        }
    }
}
