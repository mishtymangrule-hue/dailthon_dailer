package com.app.dialer.core.telecom

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Application-scoped manager that tracks the state of the currently active call
 * by consuming events from [CallEventBus].
 *
 * ### P2+ scope
 * This class is a Prompt-2 (InCallService module) concern. In P1, [CallEventBus]
 * emits no events, so all state flows remain null / idle. Full wiring via
 * [InCallServiceImpl] is implemented in Prompt 2.
 *
 * Runs in a background [CoroutineScope] with [SupervisorJob] + [Dispatchers.Default]
 * so that a failure in the event collector does not bubble up and cancel the scope.
 *
 * ### State model
 * Only one active call is tracked at a time. If a new [CallEvent.CallAdded] arrives
 * while a call is already active, the newer call overwrites the tracked state
 * (appropriate for basic single-call scenarios; conference support is out of scope).
 *
 * ### When to use
 * Inject [CallStateManager] into ViewModels or services that need to react to
 * call lifecycle changes without subscribing directly to [CallEventBus].
 */
@Singleton
class CallStateManager @Inject constructor(
    private val eventBus: CallEventBus
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _currentCallState = MutableStateFlow<CallState?>(null)
    /** Current lifecycle state of the active call, or null when no call is in progress. */
    val currentCallState: StateFlow<CallState?> = _currentCallState.asStateFlow()

    private val _activeCallId = MutableStateFlow<String?>(null)
    /** Unique ID of the currently active call, or null when idle. */
    val activeCallId: StateFlow<String?> = _activeCallId.asStateFlow()

    private val _activeCallNumber = MutableStateFlow<String?>(null)
    /** Phone number of the currently active call, or null when idle. */
    val activeCallNumber: StateFlow<String?> = _activeCallNumber.asStateFlow()

    init {
        scope.launch {
            eventBus.callEvents.collect { event ->
                when (event) {
                    is CallEvent.CallAdded -> {
                        _activeCallId.value = event.callId
                        _activeCallNumber.value = event.number
                        _currentCallState.value = when (event.direction) {
                            CallDirection.OUTGOING -> CallState.DIALING
                            CallDirection.INCOMING -> CallState.RINGING
                        }
                    }

                    is CallEvent.CallStateChanged -> {
                        // Only track state changes for the currently active call.
                        // If no active call is tracked yet, accept the first event.
                        if (_activeCallId.value == null || event.callId == _activeCallId.value) {
                            _currentCallState.value = event.state
                        }
                    }

                    is CallEvent.CallRemoved -> {
                        if (event.callId == _activeCallId.value) {
                            _currentCallState.value = CallState.DISCONNECTED
                            _activeCallId.value = null
                            _activeCallNumber.value = null
                        }
                    }

                    is CallEvent.AudioRouteChanged -> {
                        // Handled by AudioRouteManager; nothing to track here.
                    }
                }
            }
        }
    }

    // ─── Convenience queries ──────────────────────────────────────────────────

    /**
     * Returns true when the active call is in [CallState.ACTIVE] or [CallState.HOLDING].
     */
    fun isInActiveCall(): Boolean =
        _currentCallState.value == CallState.ACTIVE ||
                _currentCallState.value == CallState.HOLDING

    /**
     * Returns true when an incoming call is currently ringing.
     */
    fun isIncomingCallRinging(): Boolean =
        _currentCallState.value == CallState.RINGING

    /**
     * Returns the unique call ID of the currently tracked call, or null when idle.
     */
    fun getCurrentCallId(): String? = _activeCallId.value
}
