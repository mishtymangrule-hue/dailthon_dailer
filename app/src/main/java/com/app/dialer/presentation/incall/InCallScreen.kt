package com.app.dialer.presentation.incall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * In-call screen placeholder — full implementation in Prompt 4.
 *
 * @param callId      The call identifier passed from the NavGraph.
 * @param onCallEnded Called when the call ends and the screen should close.
 */
@Composable
fun InCallScreen(
    callId: String = "",
    onCallEnded: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "In Call",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
