package com.app.dialer.presentation.dialer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.dialer.R
import com.app.dialer.domain.model.SimCard
import com.app.dialer.presentation.dialer.components.CallButton
import com.app.dialer.presentation.dialer.components.DeleteButton
import com.app.dialer.presentation.dialer.components.DialerInputField
import com.app.dialer.presentation.dialer.components.DialerTopBar
import com.app.dialer.presentation.dialer.components.KeypadGrid
import com.app.dialer.presentation.dialer.components.SimSelectorDialog
import com.app.dialer.presentation.dialer.components.SuggestedContactsList
import com.app.dialer.presentation.dialer.components.VoicemailButton
import kotlinx.coroutines.launch

/**
 * Root composable for the Dialer / keypad screen.
 *
 * Wires [DialerViewModel] state → UI components, and routes one-time [DialerEvent]s
 * to navigation, snackbar, or dialog actions.
 *
 * @param viewModel            Hilt ViewModel, injected automatically.
 * @param onNavigateToInCall   Called with (phoneNumber, simCard?) to push the in-call screen.
 * @param onNavigateToSettings Called when the user taps Settings from the overflow menu.
 */
@Composable
fun DialerScreen(
    viewModel: DialerViewModel = hiltViewModel(),
    onNavigateToInCall: (String, SimCard?) -> Unit = { _, _ -> },
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSimSelector by remember { mutableStateOf(false) }
    var availableSims by remember { mutableStateOf<List<SimCard>>(emptyList()) }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DialerEvent.NavigateToInCall ->
                    onNavigateToInCall(event.phoneNumber, event.simCard)
                is DialerEvent.ShowSimSelector -> {
                    availableSims = event.availableSims
                    showSimSelector = true
                }
                is DialerEvent.ShowError ->
                    snackbarHostState.showSnackbar(event.message)
                is DialerEvent.ShowPermissionRationale ->
                    snackbarHostState.showSnackbar("Phone permission required to make calls")
                is DialerEvent.CopyToClipboard -> { /* handled inside DialerInputField */ }
                is DialerEvent.CallInitiated -> { /* navigation driven by NavigateToInCall */ }
            }
        }
    }

    // SIM selector dialog – rendered as an overlay above the scaffold content
    if (showSimSelector) {
        SimSelectorDialog(
            sims = availableSims,
            onSimSelected = { sim ->
                showSimSelector = false
                viewModel.onSimSelected(sim)
            },
            onDismiss = { showSimSelector = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            DialerTopBar(
                onOpenSettings = onNavigateToSettings,
                onOpenSearch = { /* TODO: open contact search */ }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Derive display state from uiState (empty defaults for Idle / Error)
        val inputState = when (val s = uiState) {
            is DialerUiState.Dialing -> s.input
            else -> DialerInputState(rawInput = "", formattedInput = "", isValid = false, cursorPosition = 0)
        }
        val suggestions = when (val s = uiState) {
            is DialerUiState.Dialing -> s.suggestions
            else -> emptyList()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── In-app logo header ────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.in_app_logo),
                    contentDescription = "Dailathon logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Dailathon",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Every Call Matters",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Phone number input field ──────────────────────────────────
            DialerInputField(
                state = inputState,
                onPasteRequest = { viewModel.onPasteInput(it) },
                modifier = Modifier.fillMaxWidth()
            )

            // ── Contact suggestions (visible only while input is non-empty)
            AnimatedVisibility(
                visible = suggestions.isNotEmpty() && inputState.rawInput.isNotEmpty(),
                enter = fadeIn(tween(180)) + slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(180)
                ),
                exit = fadeOut(tween(150)) + slideOutVertically(
                    targetOffsetY = { -it / 2 },
                    animationSpec = tween(150)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f, fill = false)
            ) {
                SuggestedContactsList(
                    contacts = suggestions,
                    onContactSelected = { viewModel.onContactSelected(it) },
                    onContactCallDirect = { contact ->
                        viewModel.onContactSelected(contact)
                        viewModel.onCallPressed()
                    }
                )
            }

            // ── Keypad ────────────────────────────────────────────────────
            KeypadGrid(
                onDigitPressed = { digit ->
                    viewModel.onDigitPressed(digit.digit)
                    viewModel.playDtmfTone(digit.digit)
                },
                onAsteriskLongPress = { viewModel.onDigitPressed(",") },
                onZeroLongPress = {
                    viewModel.onDigitPressed("+")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Bottom row: voicemail | call | delete ─────────────────────
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 24.dp, vertical = 4.dp)
            ) {
                VoicemailButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Voicemail is not yet available in this version")
                        }
                    },
                    hasUnread = false
                )
                CallButton(
                    onClick = { viewModel.onCallPressed() },
                    isEnabled = inputState.rawInput.isNotEmpty()
                )
                DeleteButton(
                    onClick = { viewModel.onDeletePressed() },
                    onLongClick = { viewModel.onDeleteLongPressed() },
                    isVisible = inputState.rawInput.isNotEmpty()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
