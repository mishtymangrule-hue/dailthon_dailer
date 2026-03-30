package com.app.dialer.core.telecom

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper that wraps [TelecomManager] calls behind a safe, OEM-variation-tolerant API.
 *
 * Every [TelecomManager] call is enclosed in a try-catch so that OEM customisations
 * which throw undocumented exceptions do not crash the app. Failures are logged with
 * the [TAG] tag and returned as [Result.failure].
 *
 * Requires [android.Manifest.permission.CALL_PHONE] for [initiateOutgoingCall] and
 * [android.Manifest.permission.READ_PHONE_STATE] for account-handle queries.
 */
@Singleton
class TelecomManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val telecomManager: TelecomManager =
        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    private val subscriptionManager: SubscriptionManager =
        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

    companion object {
        private const val TAG = "TelecomHelper"
    }

    /**
     * Places an outgoing call via [TelecomManager.placeCall].
     *
     * Builds a `tel:` URI from [phoneNumber] and attaches a [PhoneAccountHandle] when
     * [subscriptionId] is non-null. Falls back to the system-default account when
     * [subscriptionId] is null or when no matching handle can be located.
     *
     * @param phoneNumber  Raw or E.164 number to dial.
     * @param subscriptionId [android.telephony.SubscriptionManager] subscription ID of the
     *                       desired SIM, or null to use the system default.
     * @return [Result.success] when the call intent was dispatched without error;
     *         [Result.failure] wrapping a [SecurityException] or generic [Exception] on error.
     */
    fun initiateOutgoingCall(
        phoneNumber: String,
        subscriptionId: Int?
    ): Result<Unit> {
        return try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return Result.failure(SecurityException("CALL_PHONE permission not granted"))
            }

            val uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, phoneNumber, null)
            val extras = Bundle().apply {
                putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, false)
                val handle = if (subscriptionId != null) {
                    buildPhoneAccountHandleForSub(subscriptionId) ?: getDefaultPhoneAccountHandle()
                } else {
                    getDefaultPhoneAccountHandle()
                }
                if (handle != null) {
                    putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, handle)
                }
            }

            telecomManager.placeCall(uri, extras)
            Result.success(Unit)
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException placing call to $phoneNumber", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error placing call to $phoneNumber", e)
            Result.failure(e)
        }
    }

    /**
     * Returns the default outgoing [PhoneAccountHandle] for the `tel:` scheme,
     * or null when none is configured (e.g. no default SIM set).
     */
    fun getDefaultPhoneAccountHandle(): PhoneAccountHandle? {
        return try {
            telecomManager.getDefaultOutgoingPhoneAccount(PhoneAccount.SCHEME_TEL)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get default phone account handle", e)
            null
        }
    }

    /**
     * Returns all [PhoneAccountHandle]s that are capable of placing calls
     * (i.e. active SIM accounts), or an empty list on error / missing permission.
     */
    fun getAvailablePhoneAccountHandles(): List<PhoneAccountHandle> {
        return try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "READ_PHONE_STATE not granted; returning empty account list")
                return emptyList()
            }
            telecomManager.callCapablePhoneAccounts ?: emptyList()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get call-capable phone accounts", e)
            emptyList()
        }
    }

    /**
     * Returns true when there is an ongoing call (any state) reported by [TelecomManager].
     */
    fun isInCall(): Boolean {
        return try {
            telecomManager.isInCall
        } catch (e: Exception) {
            Log.w(TAG, "Failed to query isInCall from TelecomManager", e)
            false
        }
    }

    /**
     * Attempts to locate the [PhoneAccountHandle] associated with [subscriptionId].
     *
     * Strategy (API 26-compatible):
     * 1. Fetch [android.telephony.SubscriptionInfo.iccId] for the subscription.
     * 2. Match against [PhoneAccountHandle.id] — the telephony stack uses the ICC ID
     *    as the handle ID for carrier SIM accounts on AOSP and most OEMs.
     * 3. If no ICC ID match is found, fall back to matching by slot index position
     *    within the call-capable handles list.
     *
     * Returns null when [android.Manifest.permission.READ_PHONE_STATE] is denied,
     * the subscription is inactive, or no matching handle can be found.
     */
    fun buildPhoneAccountHandleForSub(subscriptionId: Int): PhoneAccountHandle? {
        return try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "READ_PHONE_STATE not granted; cannot build handle for sub $subscriptionId")
                return null
            }

            val subInfo = subscriptionManager.getActiveSubscriptionInfo(subscriptionId)
                ?: return null
            val iccId = subInfo.iccId

            val handles = telecomManager.callCapablePhoneAccounts ?: return null

            // Primary: match by ICC ID (standard AOSP telephony stack behaviour)
            if (iccId != null) {
                handles.firstOrNull { it.id == iccId }?.let { return it }
            }

            // Fallback: If the device doesn't expose ICC IDs in handle IDs,
            // use slot index as a positional hint (best-effort).
            handles.getOrNull(subInfo.simSlotIndex)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to build phone account handle for sub $subscriptionId", e)
            null
        }
    }
}
