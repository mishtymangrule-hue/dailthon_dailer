package com.app.dialer.core.telecom

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log

/**
 * Screens incoming calls before they ring.
 *
 * Registered in AndroidManifest.xml with `BIND_SCREENING_SERVICE` permission so
 * the Telecom framework calls [onScreenCall] for each incoming call.
 *
 * ### Current behaviour
 * All calls are allowed through ([CallResponse.Builder.setDisallowCall] is
 * left false). Future revisions should query the app's block-list data source
 * here and call [respondToCall] with an appropriate response.
 *
 * ### Design note
 * Hilt cannot inject into a system-bound service without `@AndroidEntryPoint`.
 * Because `CallScreeningService` is not on the list of Hilt-injectable system
 * components, we use manual entry-point injection when a data lookup is needed.
 */
class CallScreeningServiceImpl : CallScreeningService() {

    companion object {
        private const val TAG = "CallScreeningServiceImpl"
    }

    /**
     * Called by the Telecom framework synchronously when an incoming call arrives.
     *
     * We must call [respondToCall] before returning from this method (or within
     * a short executor window; see platform docs).
     */
    override fun onScreenCall(callDetails: Call.Details) {
        val number = callDetails.handle?.schemeSpecificPart ?: "Unknown"
        Log.d(TAG, "onScreenCall: $number direction=${callDetails.callDirection}")

        val isBlocked = isNumberBlocked(number)

        val response = CallResponse.Builder()
            .setDisallowCall(isBlocked)
            .setRejectCall(isBlocked)
            .setSkipNotification(isBlocked)
            .setSilenceCall(false)
            .build()

        respondToCall(callDetails, response)

        if (isBlocked) {
            Log.i(TAG, "Blocked call from: $number")
        }
    }

    /**
     * Checks if a phone number is in the block-list.
     *
     * Placeholder implementation: no numbers are currently blocked.
     * In a production system, this would query a Room-backed block-list DAO
     * for entries matching the normalized phone number.
     *
     * ### Threading
     * This method runs on the Telecom background thread, so synchronous
     * database queries are acceptable here. For more complex lookups,
     * consider pre-loading the block-list into memory on startup.
     */
    private fun isNumberBlocked(number: String): Boolean {
        // TODO: Wire in block-list DAO if/when call screening is full-featured
        // For now, all calls are allowed through
        return false
    }
}
