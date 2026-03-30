package com.app.dialer.core.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.SharedFlow

/**
 * Composable contract for all screens backed by a [BaseViewModel].
 *
 * Screens are plain @Composable functions — this interface documents the expected
 * pattern rather than enforcing Composable inheritance (which Compose does not support).
 *
 * Each screen should:
 * 1. Accept the ViewModel as a parameter (injected via hiltViewModel()).
 * 2. Collect [BaseViewModel.uiState] using [collectAsStateWithLifecycle].
 * 3. Collect [BaseViewModel.events] inside a [LaunchedEffect] for side-effects.
 *
 * Example usage in a screen composable:
 * ```
 * @Composable
 * fun DialerScreen(viewModel: DialerViewModel = hiltViewModel()) {
 *     val state by viewModel.uiState.collectAsStateWithLifecycle()
 *     CollectEvents(viewModel.events) { event -> /* handle */ }
 *     // ... render UI using state
 * }
 * ```
 */

/**
 * Utility composable that collects a [SharedFlow] of events and dispatches each
 * to [onEvent] inside a stable [LaunchedEffect].
 *
 * Placed at the top of a screen composable, just after state collection.
 *
 * @param events  The SharedFlow to collect from (typically [BaseViewModel.events]).
 * @param onEvent Lambda invoked for each emitted event value.
 */
@Composable
fun <Event : Any> CollectEvents(
    events: SharedFlow<Event>,
    onEvent: (Event) -> Unit
) {
    LaunchedEffect(events) {
        events.collect { event ->
            onEvent(event)
        }
    }
}
