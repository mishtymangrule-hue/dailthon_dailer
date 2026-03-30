package com.app.dialer.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel providing structured state management and one-time event emission.
 *
 * Subclasses define:
 * - [State]  — Immutable UI state snapshot (data class recommended).
 * - [Event]  — One-time side effects (navigation, toasts, dialogs).
 *
 * @param initialState The initial [State] instance emitted before any update.
 */
abstract class BaseViewModel<State : Any, Event : Any>(
    initialState: State
) : ViewModel() {

    // ─── State ────────────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow(initialState)

    /** Observable UI state. Collect in the Composable layer. */
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    /** Convenience accessor for the current snapshot. */
    protected val currentState: State get() = _uiState.value

    // ─── One-time events ─────────────────────────────────────────────────────

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 16)

    /**
     * One-time event stream. Use a `LaunchedEffect` in the Composable to
     * collect these without re-triggering on recomposition.
     */
    val events: SharedFlow<Event> = _events.asSharedFlow()

    // ─── Error handler ────────────────────────────────────────────────────────

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    // ─── Protected helpers ────────────────────────────────────────────────────

    /**
     * Update the UI state by applying [reducer] to the current state.
     * Thread-safe; can be called from any coroutine.
     */
    protected fun updateState(reducer: State.() -> State) {
        _uiState.value = _uiState.value.reducer()
    }

    /**
     * Emit a one-time [Event] to all active collectors.
     * Uses [MutableSharedFlow] with a small buffer to avoid suspending callers.
     */
    protected fun emitEvent(event: Event) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    /**
     * Launch a coroutine in [viewModelScope] with the shared error handler attached.
     * Uncaught exceptions are routed to [handleError].
     */
    protected fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(coroutineExceptionHandler, block = block)
    }

    /**
     * Override in subclasses to handle uncaught coroutine errors.
     * Default implementation does nothing — subclasses should at minimum log errors.
     */
    protected open fun handleError(throwable: Throwable) {
        // Default: no-op. Override to log or propagate.
    }
}
