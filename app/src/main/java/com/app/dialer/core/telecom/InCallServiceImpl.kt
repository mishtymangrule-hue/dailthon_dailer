package com.app.dialer.core.telecom

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import com.app.dialer.core.audio.AudioRouteManager
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Full implementation of [InCallService].
 *
 * Registered in AndroidManifest.xml with `BIND_INCALL_SERVICE` permission and
 * the `IN_CALL_SERVICE_UI` meta-data so the system binds to it when this app
 * is the default dialer.
 *
 * ### Architecture
 * - Owns the authoritative `Call → callId` mapping (`activeCalls` map).
 * - Publishes lifecycle events on [CallEventBus] so the in-call UI ViewModel
 *   can react without coupling to Telecom APIs.
 * - Starts / stops [CallManagerService] to maintain a foreground notification.
 * - Exposes control shims (`answerFirstRinging`, `toggleMute`, etc.) that
 *   [CallBroadcastReceiver] can invoke from notification actions.
 *
 * ### Hilt access
 * Because `InCallService` is a system-bound service, Hilt cannot inject into it
 * directly with `@AndroidEntryPoint`. We use an [EntryPoint] interface instead.
 */
class InCallServiceImpl : InCallService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ServiceEntryPoint {
        fun callEventBus(): CallEventBus
        fun callNotificationManager(): CallNotificationManager
        fun audioRouteManager(): AudioRouteManager
    }

    // ── Companion – static instance for notification-action shim ─────────────

    companion object {
        private const val TAG = "InCallServiceImpl"

        /**
         * Non-null while the service is bound by Telecom (i.e. during an active
         * or ringing call). [CallBroadcastReceiver] uses this to dispatch controls.
         *
         * Guarded by `@Volatile`; all writes happen on the main thread (Telecom
         * callbacks are delivered on the main thread).
         */
        @Volatile
        var instance: InCallServiceImpl? = null
            internal set
    }

    // ── Per-service resources ─────────────────────────────────────────────────

    private lateinit var callEventBus: CallEventBus
    private lateinit var notificationManager: CallNotificationManager
    private lateinit var audioRouteManager: AudioRouteManager

    /**
     * Coroutine scope tied to this service's lifetime.
     * Cancelled in [onDestroy].
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /**
     * Maps a stable String callId → the Telecom [Call] object.
     * Thread-safe map; values are only written on the Telecom main thread.
     */
    private val activeCalls = ConcurrentHashMap<String, Call>()

    /** Reverse mapping used inside [Call.Callback]s. */
    private val callToId = ConcurrentHashMap<Call, String>()

    // ── Service lifecycle ────────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        instance = this

        val ep = EntryPointAccessors.fromApplication(
            applicationContext, ServiceEntryPoint::class.java
        )
        callEventBus = ep.callEventBus()
        notificationManager = ep.callNotificationManager()
        audioRouteManager = ep.audioRouteManager()

        notificationManager.createNotificationChannels()
        Log.d(TAG, "onCreate")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy — clearing ${activeCalls.size} call(s)")
        instance = null
        scope.cancel()
        activeCalls.clear()
        callToId.clear()
        super.onDestroy()
    }

    // ── InCallService callbacks ──────────────────────────────────────────────

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)

        val callId = System.identityHashCode(call).toString()
        activeCalls[callId] = call
        callToId[call] = callId

        call.registerCallback(CallStateCallback(callId))

        val number = call.details?.handle?.schemeSpecificPart ?: "Unknown"
        val direction = if (call.details?.callDirection == Call.Details.DIRECTION_OUTGOING)
            CallDirection.OUTGOING else CallDirection.INCOMING

        scope.launch {
            callEventBus.emit(CallEvent.CallAdded(callId, number, direction))
        }

        // Start foreground notification service
        startCallManagerService()

        if (direction == CallDirection.INCOMING) {
            notificationManager.showIncomingCallNotification(number, null)
        }

        Log.d(TAG, "onCallAdded id=$callId number=$number direction=$direction")
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)

        val callId = callToId.remove(call) ?: System.identityHashCode(call).toString()
        activeCalls.remove(callId)

        scope.launch {
            callEventBus.emit(CallEvent.CallRemoved(callId))
        }

        notificationManager.dismissNotification(CallNotificationManager.NOTIFICATION_ID_INCOMING_CALL)

        if (activeCalls.isEmpty()) {
            stopCallManagerService()
            val number = call.details?.handle?.schemeSpecificPart ?: "Unknown"
            // Show missed-call notification only if the call was never answered
            if (call.state == Call.STATE_DISCONNECTED &&
                call.details?.callDirection != Call.Details.DIRECTION_OUTGOING
            ) {
                val disconnectCause = call.details?.disconnectCause?.code
                if (disconnectCause == android.telecom.DisconnectCause.MISSED) {
                    notificationManager.showMissedCallNotification(number, null)
                }
            }
        }

        Log.d(TAG, "onCallRemoved id=$callId active=${activeCalls.size}")
    }

    // ── Control shims (invoked by CallBroadcastReceiver) ────────────────────

    /** Answers the first call currently in [Call.STATE_RINGING]. */
    fun answerFirstRinging() {
        activeCalls.values
            .firstOrNull { it.state == Call.STATE_RINGING }
            ?.answer(0)
    }

    /** Rejects the first call currently in [Call.STATE_RINGING]. */
    fun rejectFirstRinging() {
        activeCalls.values
            .firstOrNull { it.state == Call.STATE_RINGING }
            ?.reject(false, null)
    }

    /** Disconnects all active or ringing calls. */
    fun endAllActiveCalls() {
        activeCalls.values.toList().forEach { it.disconnect() }
    }

    /** Toggles the microphone mute state. */
    @SuppressLint("NewApi")  // setMuted is available since API 21
    fun toggleMute() {
        val isMuted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // getCallAudioState() is deprecated at 33 but still functional
            @Suppress("DEPRECATION")
            callAudioState?.isMuted == true
        } else {
            @Suppress("DEPRECATION")
            callAudioState?.isMuted == true
        }
        setMuted(!isMuted)
    }

    /** Toggles between earpiece and speakerphone. */
    @Suppress("DEPRECATION")
    fun toggleSpeaker() {
        val currentRoute = callAudioState?.route ?: android.telecom.CallAudioState.ROUTE_EARPIECE
        val isSpeaker = (currentRoute and android.telecom.CallAudioState.ROUTE_SPEAKER) != 0
        setAudioRoute(
            if (isSpeaker) android.telecom.CallAudioState.ROUTE_EARPIECE
            else android.telecom.CallAudioState.ROUTE_SPEAKER
        )
        val newRoute = if (isSpeaker) AudioRoute.EARPIECE else AudioRoute.SPEAKER
        scope.launch { callEventBus.emit(CallEvent.AudioRouteChanged(newRoute)) }
    }

    // ── CallManagerService orchestration ────────────────────────────────────

    private fun startCallManagerService() {
        val intent = Intent(this, CallManagerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopCallManagerService() {
        stopService(Intent(this, CallManagerService::class.java))
    }

    // ── Call.Callback inner class ────────────────────────────────────────────

    private inner class CallStateCallback(private val callId: String) : Call.Callback() {

        override fun onStateChanged(call: Call, state: Int) {
            val mappedState = mapCallState(state)
            scope.launch {
                callEventBus.emit(CallEvent.CallStateChanged(callId, mappedState))
            }

            if (state == Call.STATE_ACTIVE) {
                val number = call.details?.handle?.schemeSpecificPart ?: "Unknown"
                notificationManager.dismissNotification(
                    CallNotificationManager.NOTIFICATION_ID_INCOMING_CALL
                )
                notificationManager.showActiveCallNotification(number, 0)
            }

            Log.d(TAG, "onStateChanged callId=$callId state=$mappedState")
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun mapCallState(telecomState: Int): CallState = when (telecomState) {
        Call.STATE_DIALING, Call.STATE_PULLING_CALL -> CallState.DIALING
        Call.STATE_RINGING                          -> CallState.RINGING
        Call.STATE_ACTIVE                           -> CallState.ACTIVE
        Call.STATE_HOLDING                          -> CallState.HOLDING
        Call.STATE_DISCONNECTING                    -> CallState.DISCONNECTING
        else                                        -> CallState.DISCONNECTED
    }
}
