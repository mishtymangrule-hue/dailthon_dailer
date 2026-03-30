package com.app.dialer.core.telecom

import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log

/**
 * Represents a single telephony connection managed by [CallConnectionService].
 *
 * ### P1 scope
 * Scaffold stub — logs state changes only. Full [CallEventBus] event emission
 * and DTMF tone delegation via [AudioRouteManager] are implemented in
 * Prompt 2 (InCallService module).
 *
 * @param callId Unique string ID assigned per call by [CallConnectionService].
 */
class CallConnection(val callId: String) : Connection() {

    companion object {
        private const val TAG = "CallConnection"
    }

    init {
        connectionCapabilities = CAPABILITY_HOLD or CAPABILITY_MUTE or CAPABILITY_SUPPORT_HOLD
    }

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)
        // P2+: emit CallEvent.CallStateChanged on CallEventBus
        Log.d(TAG, "onStateChanged: callId=$callId state=$state")
    }

    override fun onPlayDtmfTone(c: Char) {
        super.onPlayDtmfTone(c)
        // P2+: delegate to AudioRouteManager.playDtmfTone(c)
        Log.d(TAG, "onPlayDtmfTone: '$c' — P1 stub")
    }

    override fun onDisconnect() {
        super.onDisconnect()
        // P2+: emit CallEvent.CallRemoved on CallEventBus
        Log.d(TAG, "onDisconnect: callId=$callId")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }

    override fun onHold() {
        super.onHold()
        setOnHold()
        Log.d(TAG, "onHold: callId=$callId")
    }

    override fun onUnhold() {
        super.onUnhold()
        setActive()
        Log.d(TAG, "onUnhold: callId=$callId")
    }

    override fun onAnswer() {
        super.onAnswer()
        setActive()
        Log.d(TAG, "onAnswer: callId=$callId")
    }
}
