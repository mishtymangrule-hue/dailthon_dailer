package com.app.dialer.core.service

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService

/**
 * Stub declaration for the Dialer's CallScreeningService.
 *
 * This service is declared in AndroidManifest.xml. When active, the Telecom
 * framework calls [onScreenCall] before connecting an incoming call, allowing
 * the app to block or silence it.
 *
 * Full spam detection and call-screening logic will be implemented in a
 * subsequent prompt.
 *
 * API compatibility note:
 * - [CallResponse.Builder.setDisallowCall] / [CallResponse.Builder.setRejectCall] — API 24+
 * - [CallResponse.Builder.setSilenceCall] / [CallResponse.Builder.setSkipCallLog] /
 *   [CallResponse.Builder.setSkipNotification] — API 29+
 * The API 29+ methods are only called on qualifying devices to avoid [NoSuchMethodError]
 * on API 26–28.
 */
class DialerCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        // Default: allow all calls through with no modification.
        val responseBuilder = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)

        // setSilenceCall, setSkipCallLog, setSkipNotification were added in API 29.
        // Omitting them on API 26–28 leaves the platform defaults (false) in place.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            responseBuilder
                .setSilenceCall(false)
                .setSkipCallLog(false)
                .setSkipNotification(false)
        }

        respondToCall(callDetails, responseBuilder.build())
    }
}
