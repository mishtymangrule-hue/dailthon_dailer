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

        // TODO: query block-list repository and set setDisallowCall(true) for blocked numbers
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
     * Stub block-list check.
     *
     * Replace this with a real lookup against a Room-backed block-list table.
     * This method runs on the main thread, so real I/O must be pre-loaded or
     * performed on a background thread with a synchronous barrier.
     */
    private fun isNumberBlocked(number: String): Boolean {
        // Placeholder: no numbers are blocked until a block-list DAO is wired in.
        return false
    }
}
