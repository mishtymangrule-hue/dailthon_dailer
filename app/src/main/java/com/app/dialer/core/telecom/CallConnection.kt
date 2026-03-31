package com.app.dialer.core.telecom

import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import com.app.dialer.core.audio.AudioRouteManager

/**
 * Represents a single telephony connection managed by [CallConnectionService].
 *
 * Tracks state transitions via [Connection] callbacks and emits [CallEvent]s
 * to [CallEventBus] so the in-call ViewModel can react in real time.
 *
 * DTMF tone requests ([onPlayDtmfTone], [onStopDtmfTone]) are delegated to
 * an injected [AudioRouteManager] to manage device audio tone generation.
 *
 * @param callId     Unique string ID assigned per call by [CallConnectionService].
 * @param eventBus   Event bus for publishing call state changes.
 * @param audioManager Audio manager for DTMF tone playback.
 */
class CallConnection(
    val callId: String,
    private val eventBus: CallEventBus,
    private val audioManager: AudioRouteManager
) : Connection() {

    companion object {
        private const val TAG = "CallConnection"
    }

    init {
        connectionCapabilities = CAPABILITY_HOLD or CAPABILITY_MUTE or CAPABILITY_SUPPORT_HOLD
        // Not self-managed for standard cellular calls
        connectionProperties = 0
    }

    // ─── State transitions ─────────────────────────────────────────────────

    /**
     * Called by the Telecom stack when the call state changes.
     * Maps [Connection.STATE_*] constants to [CallState] enum and emits via bus.
     */
    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)
        val mappedState = mapState(state)
        Log.d(TAG, "onStateChanged: callId=$callId state=$state (mapped to $mappedState)")
        eventBus.emitSync(CallEvent.CallStateChanged(callId, mappedState))
    }

    /**
     * Maps [Connection] STATE_* constants to [CallState] enum values.
     * Falls back to [CallState.ACTIVE] for unrecognized values.
     */
    private fun mapState(telephonyState: Int): CallState = when (telephonyState) {
        STATE_DIALING -> CallState.DIALING
        STATE_RINGING -> CallState.RINGING
        STATE_ACTIVE -> CallState.ACTIVE
        STATE_HOLDING -> CallState.HOLDING
        STATE_DISCONNECTING -> CallState.DISCONNECTING
        STATE_DISCONNECTED -> CallState.DISCONNECTED
        else -> {
            Log.w(TAG, "Unknown telephony state: $telephonyState")
            CallState.ACTIVE
        }
    }

    // ─── DTMF tone control ────────────────────────────────────────────────

    /**
     * Called when the remote party or local user requests a DTMF tone.
     * Delegates to [AudioRouteManager.playDtmfTone].
     *
     * @param c DTMF digit character (0-9, *, #, A-D).
     */
    override fun onPlayDtmfTone(c: Char) {
        super.onPlayDtmfTone(c)
        Log.d(TAG, "onPlayDtmfTone: callId=$callId digit='$c'")
        audioManager.playDtmfTone(c)
    }

    /**
     * Called to stop any ongoing DTMF tone playback.
     * Delegates to [AudioRouteManager.stopDtmfTone].
     */
    override fun onStopDtmfTone() {
        super.onStopDtmfTone()
        Log.d(TAG, "onStopDtmfTone: callId=$callId")
        audioManager.stopDtmfTone()
    }

    // ─── Call control ─────────────────────────────────────────────────────

    /**
     * Called when the user or remote party hangs up the call.
     * Emits [CallEvent.CallRemoved] via the bus and cleans up resources.
     */
    override fun onDisconnect() {
        super.onDisconnect()
        Log.d(TAG, "onDisconnect: callId=$callId")
        eventBus.emitSync(CallEvent.CallRemoved(callId))
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }

    /** Called when the user requests hold. */
    override fun onHold() {
        super.onHold()
        setOnHold()
        Log.d(TAG, "onHold: callId=$callId")
        eventBus.emitSync(CallEvent.CallStateChanged(callId, CallState.HOLDING))
    }

    /** Called when the user resumes a held call. */
    override fun onUnhold() {
        super.onUnhold()
        setActive()
        Log.d(TAG, "onUnhold: callId=$callId")
        eventBus.emitSync(CallEvent.CallStateChanged(callId, CallState.ACTIVE))
    }

    /** Called when the user answers an incoming call. */
    override fun onAnswer() {
        super.onAnswer()
        setActive()
        Log.d(TAG, "onAnswer: callId=$callId")
        eventBus.emitSync(CallEvent.CallStateChanged(callId, CallState.ACTIVE))
    }

    /** Called when the user declines an incoming call. */
    override fun onAbort() {
        super.onAbort()
        Log.d(TAG, "onAbort: callId=$callId")
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
    }

    /** Called when the user requests to separate this call from a conference. */
    override fun onSeparate() {
        super.onSeparate()
        Log.d(TAG, "onSeparate: callId=$callId (no-op for P2 single-call mode)")
    }
}
