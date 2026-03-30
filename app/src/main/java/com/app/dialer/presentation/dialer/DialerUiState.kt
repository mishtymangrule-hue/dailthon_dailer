package com.app.dialer.presentation.dialer

import com.app.dialer.domain.model.SimCard
import com.app.dialer.domain.model.SuggestedContact
import com.app.dialer.domain.usecase.DialerError

/**
 * Represents all possible visual states for the Dialer screen.
 *
 * State transitions:
 * ```
 * Idle ──(digit pressed)──► Dialing ──(call pressed + success)──► CallInitiated
 *                                   └──(call pressed + error)───► Error
 * Error ──(digit pressed)──► Dialing
 * CallInitiated ──(navigation complete)──► [screen popped / back]
 * ```
 */
sealed interface DialerUiState {

    /** No input entered; shows recent calls and idle suggestions. */
    object Idle : DialerUiState

    /**
     * User is actively entering a phone number.
     *
     * @param input         Current input state including raw, formatted, and validity.
     * @param suggestions   Live contact suggestions for the current query.
     * @param selectedSim   The SIM card the user has explicitly selected, or null to
     *                      use the system default.
     */
    data class Dialing(
        val input: DialerInputState,
        val suggestions: List<SuggestedContact>,
        val selectedSim: SimCard?
    ) : DialerUiState

    /**
     * A call has been successfully dispatched to the Telecom stack.
     *
     * The ViewModel simultaneously emits [DialerEvent.NavigateToInCall] so the UI
     * navigates to the in-call screen while this state is active.
     *
     * @param phoneNumber The dialled number (raw input as typed).
     * @param simCard     The SIM used to place the call, or null for system default.
     */
    data class CallInitiated(
        val phoneNumber: String,
        val simCard: SimCard?
    ) : DialerUiState

    /**
     * An error occurred during call initiation or input validation.
     *
     * @param error Domain-level error describing the failure.
     */
    data class Error(val error: DialerError) : DialerUiState
}

/**
 * Immutable snapshot of the dial-pad input field state.
 *
 * @param rawInput       The unformatted string as typed by the user.
 * @param formattedInput Locale-aware display string (e.g. "+91 98765 43210").
 * @param isValid        True when [rawInput] passes [com.app.dialer.domain.usecase.ValidatePhoneNumberUseCase].
 * @param cursorPosition Current cursor position within [rawInput] (0-based, end by default).
 */
data class DialerInputState(
    val rawInput: String,
    val formattedInput: String,
    val isValid: Boolean,
    val cursorPosition: Int
)
