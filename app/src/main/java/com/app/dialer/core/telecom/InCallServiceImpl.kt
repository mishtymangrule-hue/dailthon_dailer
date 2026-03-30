package com.app.dialer.core.telecom

import android.telecom.Call
import android.telecom.InCallService
import android.util.Log

/**
 * Receives call lifecycle events from the Telecom framework.
 *
 * Registered in AndroidManifest.xml with `BIND_INCALL_SERVICE` permission and
 * the `IN_CALL_SERVICE_UI` meta-data so the system binds to it when this app
 * is the default dialer.
 *
 * ### P1 scope
 * P1 scaffold stub — logs call events only. Full call-event routing via
 * [CallEventBus], audio management via [AudioRouteManager], notification
 * management via [CallNotificationManager], and in-call UI wiring are
 * implemented in Prompt 2 (InCallService module).
 */
class InCallServiceImpl : InCallService() {

    companion object {
        private const val TAG = "InCallServiceImpl"

        /**
         * Non-null while the service is bound by Telecom (i.e. during an active
         * or ringing call). Reserved for P2+ notification-action shims.
         */
        @Volatile
        var instance: InCallServiceImpl? = null
            internal set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "onCreate — P1 scaffold stub")
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        // P2+: wire to CallEventBus, show in-call UI, start CallManagerService
        val number = call.details?.handle?.schemeSpecificPart ?: "Unknown"
        Log.d(TAG, "onCallAdded: number=$number")
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        // P2+: remove from active-call map, stop CallManagerService, show missed-call notification
        Log.d(TAG, "onCallRemoved")
    }
}
