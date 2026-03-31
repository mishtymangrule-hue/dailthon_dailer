package com.app.dialer.core.telecom

import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import dagger.hilt.android.EntryPoint
import dagger.hilt.android.EntryPointAccessors
import com.app.dialer.core.audio.AudioRouteManager
import java.util.concurrent.ConcurrentHashMap

/**
 * Receives call lifecycle events from the Telecom framework and manages the
 * complete call state including audio routing and call controls.
 *
 * Registered in AndroidManifest.xml with `BIND_INCALL_SERVICE` permission and
 * the `IN_CALL_SERVICE_UI` meta-data so the system binds to it when this app
 * is the default dialer.
 *
 * Responsibilities:
 * - Route Telecom call events (onCallAdded, onCallRemoved) to [CallEventBus].
 * - Maintain a live map of active calls keyed by call ID.
 * - Provide call control methods (answer, reject, end, hold, mute, audio route).
 * - Manage call audio state and route transitions.
 * - Coordinate with [CallManagerService] for foreground notification.
 */
class InCallServiceImpl : InCallService() {

    companion object {
        private const val TAG = "InCallServiceImpl"

        @Volatile
        var instance: InCallServiceImpl? = null
            internal set

        @EntryPoint
        interface InCallServiceEntryPoint {
            fun eventBus(): CallEventBus
            fun audioManager(): AudioRouteManager
        }
    }

    private lateinit var entryPoint: InCallServiceEntryPoint
    private val activeCalls = ConcurrentHashMap<String, Call>()
    private val callCallbacks = mutableMapOf<String, CallStateCallback>()

    override fun onCreate() {
        super.onCreate()
        instance = this
        entryPoint = EntryPointAccessors.fromApplication(
            this,
            InCallServiceEntryPoint::class.java
        )
        Log.d(TAG, "onCreate")
    }

    override fun onDestroy() {
        instance = null
        activeCalls.forEach { (callId, call) ->
            call.unregisterCallback(callCallbacks[callId])
        }
        activeCalls.clear()
        callCallbacks.clear()
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        val number = call.details?.handle?.schemeSpecificPart ?: "Unknown"
        val callId = call.details?.let { details ->
            "${details.handle?.authority}_${details.handle?.schemeSpecificPart}"
        } ?: "unknown_${System.currentTimeMillis()}"
        
        activeCalls[callId] = call
        
        // Register callback for state changes
        val callback = CallStateCallback(callId, number)
        callCallbacks[callId] = callback
        call.registerCallback(callback)
        
        // Emit event
        val direction = if (call.state == Call.STATE_RINGING) {
            CallDirection.INCOMING
        } else {
            CallDirection.OUTGOING
        }
        
        entryPoint.eventBus().emitSync(
            CallEvent.CallAdded(
                callId = callId,
                number = number,
                direction = direction
            )
        )
        
        Log.d(TAG, "onCallAdded: callId=$callId number=$number state=${call.state}")
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        
        // Find and remove from active calls
        val callId = activeCalls.entries.find { it.value == call }?.key
        if (callId != null) {
            activeCalls.remove(callId)
            callCallbacks.remove(callId)?.let { callback ->
                call.unregisterCallback(callback)
            }
            
            entryPoint.eventBus().emitSync(
                CallEvent.CallRemoved(callId = callId)
            )
        }
        
        Log.d(TAG, "onCallRemoved: callId=$callId")
    }

    // ─── Call control methods ────────────────────────────────────────────────

    /**
     * Answers an incoming call.
     */
    fun answerCall(callId: String) {
        val call = activeCalls[callId]
        if (call != null && call.state == Call.STATE_RINGING) {
            call.answer(Call.VideoState.AUDIO_ONLY)
            Log.d(TAG, "answerCall: $callId")
        }
    }

    /**
     * Rejects an incoming call.
     */
    fun rejectCall(callId: String) {
        val call = activeCalls[callId]
        if (call != null && call.state == Call.STATE_RINGING) {
            call.reject(false)
            Log.d(TAG, "rejectCall: $callId")
        }
    }

    /**
     * Ends the specified call.
     */
    fun endCall(callId: String) {
        val call = activeCalls[callId]
        if (call != null) {
            call.disconnect()
            Log.d(TAG, "endCall: $callId")
        }
    }

    /**
     * Places a call on hold.
     */
    fun holdCall(callId: String) {
        val call = activeCalls[callId]
        if (call != null && call.state == Call.STATE_ACTIVE) {
            call.hold()
            Log.d(TAG, "holdCall: $callId")
        }
    }

    /**
     * Resumes a held call.
     */
    fun unholdCall(callId: String) {
        val call = activeCalls[callId]
        if (call != null && call.state == Call.STATE_ON_HOLD) {
            call.unhold()
            Log.d(TAG, "unholdCall: $callId")
        }
    }

    /**
     * Mutes or unmutes the call.
     */
    fun muteCall(callId: String, isMuted: Boolean) {
        val callAudioState = callAudioState
        if (callAudioState != null) {
            setMuted(isMuted)
            Log.d(TAG, "muteCall: $callId muted=$isMuted")
        }
    }

    /**
     * Sets the audio route (speaker, earpiece, bluetooth, etc.).
     */
    fun setAudioRoute(callId: String, route: Int) {
        val callAudioState = callAudioState
        if (callAudioState != null) {
            setAudioRoute(route)
            Log.d(TAG, "setAudioRoute: $callId route=$route")
        }
    }

    // ─── Inner callback class ────────────────────────────────────────────────

    /**
     * Monitors a single call's state and route changes.
     */
    private inner class CallStateCallback(
        private val callId: String,
        private val number: String
    ) : Call.Callback() {

        override fun onStateChanged(call: Call, state: Int) {
            val callState = mapTelecomState(state)
            entryPoint.eventBus().emitSync(
                CallEvent.CallStateChanged(
                    callId = callId,
                    state = callState
                )
            )
            Log.d(TAG, "onStateChanged: $callId state=$state")
        }

        override fun onDetailsChanged(call: Call, details: Call.Details) {
            // Can observe video state, capabilities, etc. here
            Log.d(TAG, "onDetailsChanged: $callId")
        }

        private fun mapTelecomState(state: Int): CallState {
            return when (state) {
                Call.STATE_NEW -> CallState.NEW
                Call.STATE_CONNECTING -> CallState.CONNECTING
                Call.STATE_ACTIVE -> CallState.ACTIVE
                Call.STATE_RINGING -> CallState.RINGING
                Call.STATE_ON_HOLD -> CallState.ON_HOLD
                Call.STATE_DISCONNECTING -> CallState.DISCONNECTING
                Call.STATE_DISCONNECTED -> CallState.DISCONNECTED
                else -> CallState.UNKNOWN
            }
        }
    }
}
