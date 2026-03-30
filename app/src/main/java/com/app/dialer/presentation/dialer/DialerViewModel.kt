package com.app.dialer.presentation.dialer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dialer.core.audio.AudioRouteManager
import com.app.dialer.domain.model.RecentCall
import com.app.dialer.domain.model.SimCard
import com.app.dialer.domain.model.SuggestedContact
import com.app.dialer.domain.usecase.CallResult
import com.app.dialer.domain.usecase.DialerError
import com.app.dialer.domain.usecase.FormatPhoneNumberUseCase
import com.app.dialer.domain.usecase.GetAvailableSimCardsUseCase
import com.app.dialer.domain.usecase.GetRecentCallsUseCase
import com.app.dialer.domain.usecase.GetSuggestedContactsUseCase
import com.app.dialer.domain.usecase.InitiateCallUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Dialer screen.
 *
 * Orchestrates dial-pad input, contact suggestion lookup, SIM card selection,
 * and call initiation. Exposes two public streams:
 *  - [uiState]: a [StateFlow] describing the current screen configuration.
 *  - [events]: a replay-0 [SharedFlow] for one-shot UI side effects.
 *
 * ### Input flow
 * Raw input changes are debounced 300 ms before triggering a contact suggestion
 * lookup via [GetSuggestedContactsUseCase]. Formatting runs on every input change
 * (via [FormatPhoneNumberUseCase]) on [Dispatchers.IO].
 *
 * ### Call initiation flow
 * `onCallPressed()` checks CALL_PHONE permission → resolves SIM card →
 * delegates to [InitiateCallUseCase]. Multi-SIM devices that have not already
 * chosen a SIM receive a [DialerEvent.ShowSimSelector] event; the flow resumes
 * when [onSimSelected] is called.
 */
@HiltViewModel
class DialerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val initiateCall: InitiateCallUseCase,
    private val getSuggestedContacts: GetSuggestedContactsUseCase,
    private val getRecentCalls: GetRecentCallsUseCase,
    private val formatPhoneNumber: FormatPhoneNumberUseCase,
    private val getAvailableSimCards: GetAvailableSimCardsUseCase,
    private val audioRouteManager: AudioRouteManager
) : ViewModel() {

    // ─── Internal mutable state ────────────────────────────────────────────────

    private val _rawInput = MutableStateFlow("")
    private val _selectedSim = MutableStateFlow<SimCard?>(null)
    private val _suggestions = MutableStateFlow<List<SuggestedContact>>(emptyList())

    /** Guard that prevents [observeUiState] from overwriting terminal states. */
    private var isCallInProgress = false

    // ─── Public state ──────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow<DialerUiState>(DialerUiState.Idle)
    val uiState: StateFlow<DialerUiState> = _uiState.asStateFlow()

    /**
     * One-time UI events. Collect with `LaunchedEffect(Unit)` in a Composable.
     * replay = 0 ensures events are not re-delivered on recomposition.
     */
    private val _events = MutableSharedFlow<DialerEvent>(replay = 0, extraBufferCapacity = 16)
    val events: SharedFlow<DialerEvent> = _events.asSharedFlow()

    /** Cached recent calls for display in the idle state. */
    val recentCalls: StateFlow<List<RecentCall>> = getRecentCalls()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        observeSuggestions()
        observeUiState()
    }

    // ─── Observers ─────────────────────────────────────────────────────────────

    /**
     * Debounces [_rawInput] by 300 ms then fetches fresh contact suggestions.
     * Runs on [Dispatchers.IO]; failures are swallowed (empty list fallback).
     */
    private fun observeSuggestions() {
        viewModelScope.launch {
            _rawInput
                .debounce(300L)
                .flatMapLatest { query -> getSuggestedContacts(query) }
                .flowOn(Dispatchers.IO)
                .catch { emit(emptyList()) }
                .collect { _suggestions.value = it }
        }
    }

    /**
     * Combines raw input, suggestions, and selected SIM into a [DialerUiState].
     * Formatting runs on [Dispatchers.IO]; state is posted on the Main thread.
     * Skips updates while a call is in progress to avoid clobbering terminal states.
     */
    private fun observeUiState() {
        viewModelScope.launch {
            combine(_rawInput, _suggestions, _selectedSim) { input, suggestions, sim ->
                Triple(input, suggestions, sim)
            }
                .map { (input, suggestions, sim) ->
                    if (input.isEmpty()) {
                        DialerUiState.Idle
                    } else {
                        val phoneNumber = formatPhoneNumber(input)
                        DialerUiState.Dialing(
                            input = DialerInputState(
                                rawInput = input,
                                formattedInput = phoneNumber.formatted,
                                isValid = phoneNumber.isValid,
                                cursorPosition = input.length
                            ),
                            suggestions = suggestions,
                            selectedSim = sim
                        )
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect { state ->
                    if (!isCallInProgress) {
                        _uiState.value = state
                    }
                }
        }
    }

    // ─── User actions ──────────────────────────────────────────────────────────

    /** Appends [digit] to the current input. */
    fun onDigitPressed(digit: String) {
        _rawInput.update { it + digit }
    }

    /** Removes the last character from the current input. */
    fun onDeletePressed() {
        _rawInput.update { if (it.isNotEmpty()) it.dropLast(1) else it }
    }

    /** Clears the entire input field (long-press delete). */
    fun onDeleteLongPressed() {
        clearInput()
    }

    /**
     * Initiates an outgoing call with the current input.
     *
     * Flow: permission check → SIM resolution → [InitiateCallUseCase].
     * Emits [DialerEvent.ShowSimSelector] and suspends if multi-SIM selection
     * is required; resumes via [onSimSelected].
     */
    fun onCallPressed() {
        val currentInput = _rawInput.value
        if (currentInput.isBlank()) return

        viewModelScope.launch {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                _events.emit(DialerEvent.ShowPermissionRationale)
                return@launch
            }

            val simToUse = resolveSimCard() ?: return@launch

            isCallInProgress = true
            initiateCall(currentInput, simToUse)
                .flowOn(Dispatchers.IO)
                .collect { result -> handleCallResult(result, currentInput, simToUse) }
        }
    }

    /**
     * Called after the user picks a SIM from the [DialerEvent.ShowSimSelector] sheet.
     * Stores the selection and re-triggers the call.
     */
    fun onSimSelected(simCard: SimCard) {
        _selectedSim.value = simCard
        val currentInput = _rawInput.value
        if (currentInput.isBlank()) return

        viewModelScope.launch {
            isCallInProgress = true
            initiateCall(currentInput, simCard)
                .flowOn(Dispatchers.IO)
                .collect { result -> handleCallResult(result, currentInput, simCard) }
        }
    }

    /**
     * Sanitizes [text] (keeps digits, +, *, #) and replaces the current input.
     * Also resets any previously selected SIM.
     */
    fun onPasteInput(text: String) {
        val sanitized = text.filter { it.isDigit() || it == '+' || it == '*' || it == '#' }
        _rawInput.value = sanitized
        _selectedSim.value = null
    }

    /** Fills the dial-pad with the contact's primary phone number. */
    fun onContactSelected(contact: SuggestedContact) {
        _rawInput.value = contact.phoneNumber
        _selectedSim.value = null
    }

    /** Clears all input and resets call-in-progress guard. */
    fun clearInput() {
        _rawInput.value = ""
        _selectedSim.value = null
        isCallInProgress = false
    }

    /**
     * Plays the DTMF tone for [digit] using [AudioRouteManager].
     * Runs on [Dispatchers.IO] to avoid blocking the Main thread.
     *
     * @param digit Single character string from the dial pad (0–9, *, #).
     */
    fun playDtmfTone(digit: String) {
        val char = digit.firstOrNull() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            audioRouteManager.playDtmfTone(char)
        }
    }

    // ─── Private helpers ───────────────────────────────────────────────────────

    /**
     * Returns the SIM to use for the outgoing call.
     * - If the user already selected a SIM, returns it immediately.
     * - If only one (or zero) SIMs are active, returns that SIM (or null = system default).
     * - If two or more SIMs are active, emits [DialerEvent.ShowSimSelector] and returns
     *   null — the caller must abort and wait for [onSimSelected].
     */
    private suspend fun resolveSimCard(): SimCard? {
        _selectedSim.value?.let { return it }

        val availableSims = try {
            getAvailableSimCards().first()
        } catch (e: Exception) {
            emptyList()
        }

        return when {
            availableSims.size > 1 -> {
                _events.emit(DialerEvent.ShowSimSelector(availableSims))
                null
            }
            else -> availableSims.firstOrNull()
        }
    }

    private suspend fun handleCallResult(
        result: CallResult,
        phoneNumber: String,
        simCard: SimCard?
    ) {
        when (result) {
            CallResult.Loading -> Unit

            CallResult.Success -> {
                _uiState.value = DialerUiState.CallInitiated(phoneNumber, simCard)
                _events.emit(DialerEvent.CallInitiated)
                _events.emit(DialerEvent.NavigateToInCall(phoneNumber, simCard))
            }

            is CallResult.Failure -> {
                isCallInProgress = false
                _uiState.value = DialerUiState.Error(result.error)
                when (result.error) {
                    DialerError.PermissionDenied ->
                        _events.emit(DialerEvent.ShowPermissionRationale)
                    DialerError.InvalidNumber ->
                        _events.emit(DialerEvent.ShowError("Invalid phone number"))
                    DialerError.SimUnavailable ->
                        _events.emit(DialerEvent.ShowError("SIM card unavailable"))
                    is DialerError.Unknown ->
                        _events.emit(DialerEvent.ShowError(result.error.message))
                }
            }
        }
    }
}
