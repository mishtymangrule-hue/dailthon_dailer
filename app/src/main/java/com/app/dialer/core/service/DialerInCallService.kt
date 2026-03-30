package com.app.dialer.core.service

import android.telecom.Call
import android.telecom.InCallService
import android.util.Log

/**
 * Stub declaration for the Dialer's InCallService.
 *
 * This service is declared in AndroidManifest.xml and binds to the Android
 * Telecom framework when this app is set as the default dialer.
 *
 * Full in-call UI logic (audio routing, DTMF, hold/mute, call state management)
 * will be implemented in a subsequent prompt.
 */
class DialerInCallService : InCallService() {

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        Log.d(TAG, "onCallAdded: ${call.details?.handle}")
        // CallManager integration and InCall UI launch — implemented in Prompt N.
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        Log.d(TAG, "onCallRemoved: ${call.details?.handle}")
        // CallManager cleanup — implemented in Prompt N.
    }

    companion object {
        private const val TAG = "DialerInCallService"
    }
}
