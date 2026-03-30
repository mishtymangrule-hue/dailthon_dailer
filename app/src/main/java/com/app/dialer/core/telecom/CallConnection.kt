package com.app.dialer.core.telecom

import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import com.app.dialer.core.audio.AudioRouteManager

/**
 * Represents a single telephony connection managed by [CallConnectionService].
 *
 * Each instance corresponds to one call leg. [CallEventBus] is used to broadcast
 * state changes to the rest of the application (ViewModel, notification service, etc.)
 * without tight coupling.
 *
 * ### Capabilities
 * - [CAPABILITY_HOLD]: call can be placed on hold.
 * - [CAPABILITY_MUTE]: microphone can be muted.
 * - [CAPABILITY_SUPPORT_HOLD]: UI should show a hold button.
 *
 * ### Not self-managed
 * [PROPERTY_SELF_MANAGED] is intentionally NOT set — this connection participates in the
 * standard Android telecom stack and allows system UI (dialer, notification shade) to
 * manage it.
 *
 * ### Construction
 * Hilt cannot directly inject a [Connection] subclass (the Telecom framework
 * instantiates connections via [CallConnectionService]). Dependencies are passed
 * manually from the service using its Hilt EntryPoint.
 *
 * @param eventBus          Application-scoped bus for broadcasting call events.
 * @param audioRouteManager Handles DTMF tone generation and audio routing.
 * @param callId            Unique string ID assigned per call by [CallConnectionService].
 */
class CallConnection(
    private val eventBus: CallEventBus,
    private val audioRouteManager: AudioRouteManager,
    val callId: String
) : Connection() {

    companion object {
        private const val TAG = "CallConnection"
    }

    init {
        connectionCapabilities = CAPABILITY_HOLD or CAPABILITY_MUTE or CAPABILITY_SUPPORT_HOLD
    }

    // ─── Connection callbacks (called by the Telecom framework) ───────────────

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)
        val callState = mapState(state)
        Log.d(TAG, "onStateChanged: callId=$callId state=$callState")
        eventBus.emitSync(CallEvent.CallStateChanged(callId, callState))
    }

    override fun onPlayDtmfTone(c: Char) {
        super.onPlayDtmfTone(c)
        audioRouteManager.playDtmfTone(c)
    }

    override fun onStopDtmfTone() {
        super.onStopDtmfTone()
        // AudioRouteManager auto-stops the tone after its fixed 150 ms duration.
    }

    override fun onDisconnect() {
        super.onDisconnect()
        Log.d(TAG, "onDisconnect: callId=$callId")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
        eventBus.emitSync(CallEvent.CallRemoved(callId))
    }

    override fun onHold() {
        super.onHold()
        setOnHold()
        eventBus.emitSync(CallEvent.CallStateChanged(callId, CallState.HOLDING))
    }

    override fun onUnhold() {
        super.onUnhold()
        setActive()
        eventBus.emitSync(CallEvent.CallStateChanged(callId, CallState.ACTIVE))
    }

    override fun onAnswer() {
        super.onAnswer()
        setActive()
        eventBus.emitSync(CallEvent.CallStateChanged(callId, CallState.ACTIVE))
    }

    override fun onAbort() {
        super.onAbort()
        Log.d(TAG, "onAbort: callId=$callId")
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
        eventBus.emitSync(CallEvent.CallRemoved(callId))
    }

    override fun onSeparate() {
        super.onSeparate()
        Log.d(TAG, "onSeparate: callId=$callId")
        // After conference separation the connection becomes active again.
        eventBus.emitSync(CallEvent.CallStateChanged(callId, CallState.ACTIVE))
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun mapState(state: Int): CallState = when (state) {
        STATE_DIALING      -> CallState.DIALING
        STATE_RINGING      -> CallState.RINGING
        STATE_ACTIVE       -> CallState.ACTIVE
        STATE_HOLDING      -> CallState.HOLDING
        STATE_DISCONNECTED -> CallState.DISCONNECTED
        else               -> CallState.ACTIVE
    }
}
