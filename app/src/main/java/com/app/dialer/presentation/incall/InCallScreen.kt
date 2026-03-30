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
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header bento card
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .neumorphicSurface(cornerRadius = 20.dp, elevation = 8.dp)
        ) {
            Text(
                text = "In Call",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        // Example: Empty state bento card (replace with real call UI in full impl)
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .neumorphicSurface(cornerRadius = 20.dp, elevation = 6.dp)
        ) {
            Text(
                text = "Call in progress. Call controls and info will appear here.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            )
        }

        // Add more bento cards for controls, info, etc. as needed
    }
}
