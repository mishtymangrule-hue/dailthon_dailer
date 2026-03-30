package com.app.dialer.presentation.dialer

import com.app.dialer.domain.model.SimCard

/**
 * One-time UI events emitted by [DialerViewModel] via a replay-0 [kotlinx.coroutines.flow.SharedFlow].
 *
 * Each event is consumed exactly once by the UI layer. The Compose collector should use
 * `LaunchedEffect(Unit)` with `events.collect { ... }` so events are processed in FIFO order
 * and no event is silently dropped.
 */
sealed interface DialerEvent {

    /**
     * Navigate to the in-call screen immediately after the call intent is dispatched.
     *
     * @param phoneNumber The dialled number string.
     * @param simCard     The SIM used (null → system default).
     */
    data class NavigateToInCall(
        val phoneNumber: String,
        val simCard: SimCard?
    ) : DialerEvent

    /**
     * Show a bottom-sheet / dialog that lets the user pick a SIM card before dialling.
     *
     * Once the user selects a SIM, call [DialerViewModel.onSimSelected].
     *
     * @param availableSims All currently active SIM cards reported by the device.
     */
    data class ShowSimSelector(val availableSims: List<SimCard>) : DialerEvent

    /**
     * Show a snackbar or error dialog with a human-readable error message.
     *
     * @param message Localised or plain-English description of the problem.
     */
    data class ShowError(val message: String) : DialerEvent

    /**
     * Copy [text] to the system clipboard (e.g. after a long-press on the number field).
     *
     * @param text The string content to place on the clipboard.
     */
    data class CopyToClipboard(val text: String) : DialerEvent

    /**
     * Show a rationale dialog explaining why [android.Manifest.permission.CALL_PHONE]
     * (and related permissions) are required, then request them.
     */
    object ShowPermissionRationale : DialerEvent

    /**
     * The call has been successfully initiated and handed off to the Telecom stack.
     *
     * Emitted alongside [NavigateToInCall] so callers can independently react to
     * the initiation event (e.g. analytics, haptic feedback).
     */
    object CallInitiated : DialerEvent
}
