package com.app.dialer.core.telecom

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

// ─── Enums ─────────────────────────────────────────────────────────────────────

/** Direction of the call as observed by the dialer. */
enum class CallDirection { INCOMING, OUTGOING }

/**
 * Lifecycle state of an active call.
 *
 * Mirrors [android.telecom.Connection] STATE_* constants via [CallConnection.mapState].
 */
enum class CallState {
    /** Outgoing call is being connected; remote end is ringing. */
    DIALING,
    /** Incoming call is ringing at the local end. */
    RINGING,
    /** Both parties are connected; audio is flowing. */
    ACTIVE,
    /** Call is on hold. */
    HOLDING,
    /** Call is in the process of disconnecting. */
    DISCONNECTING,
    /** Call has ended and resources have been released. */
    DISCONNECTED
}

/** Physical or logical audio output currently in use. */
enum class AudioRoute { EARPIECE, SPEAKER, BLUETOOTH, WIRED_HEADSET }

// ─── Events ────────────────────────────────────────────────────────────────────

/**
 * Domain event emitted by [CallConnection] and [CallConnectionService] to notify
 * observers of call lifecycle changes.
 */
sealed class CallEvent {

    /**
     * A new call has been established by the Telecom stack.
     *
     * @param callId    Unique string ID generated per call.
     * @param number    Phone number string (may be "Unknown" for hidden callers).
     * @param direction Whether the call is [CallDirection.OUTGOING] or [CallDirection.INCOMING].
     */
    data class CallAdded(
        val callId: String,
        val number: String,
        val direction: CallDirection
    ) : CallEvent()

    /**
     * The state of an existing call has changed.
     *
     * @param callId Identifies the affected call.
     * @param state  New [CallState] value.
     */
    data class CallStateChanged(
        val callId: String,
        val state: CallState
    ) : CallEvent()

    /**
     * A call has been fully disconnected and removed from the Telecom stack.
     *
     * @param callId Identifies the call that ended.
     */
    data class CallRemoved(val callId: String) : CallEvent()

    /**
     * The active audio output route has changed.
     *
     * @param route New [AudioRoute].
     */
    data class AudioRouteChanged(val route: AudioRoute) : CallEvent()
}

// ─── Bus ───────────────────────────────────────────────────────────────────────

/**
 * Application-scoped event bus for in-call events.
 *
 * Uses a [SharedFlow] with `replay = 1` so that late subscribers (e.g. the
 * in-call ViewModel after configuration change) immediately receive the most
 * recent event, and `extraBufferCapacity = 10` to absorb brief bursts (e.g.
 * rapid state transitions during call setup) without blocking emitters.
 *
 * Emitters inside [android.telecom.Connection] callbacks use [emitSync] since
 * those callbacks are not coroutine-suspended. Emitters inside coroutines use
 * the suspending [emit].
 */
@Singleton
class CallEventBus @Inject constructor() {

    private val _callEvents = MutableSharedFlow<CallEvent>(
        replay = 1,
        extraBufferCapacity = 10
    )

    /** Hot stream of [CallEvent]s. Subscribe with [SharedFlow.collect]. */
    val callEvents: SharedFlow<CallEvent> = _callEvents.asSharedFlow()

    /**
     * Suspends until the event is buffered. Safe to call from any coroutine context.
     */
    suspend fun emit(event: CallEvent) {
        _callEvents.emit(event)
    }

    /**
     * Non-suspending best-effort emit. Returns true on success, false when the
     * buffer is full (> 10 in-flight events). Use from [android.telecom.Connection]
     * callbacks and other non-coroutine contexts.
     */
    fun emitSync(event: CallEvent): Boolean = _callEvents.tryEmit(event)
}
