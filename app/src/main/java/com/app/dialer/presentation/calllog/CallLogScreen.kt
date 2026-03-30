package com.app.dialer.presentation.calllog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Call log screen placeholder — full implementation in Prompt 3.
 *
 * @param onCallClick  Called with a phone number when the user taps a call log entry.
 */
@Composable
fun CallLogScreen(
    onCallClick: (String) -> Unit = {}
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
                text = "Recents",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        // Example: Empty state bento card (replace with real list in full impl)
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .neumorphicSurface(cornerRadius = 20.dp, elevation = 6.dp)
        ) {
            Text(
                text = "No recent calls yet. Your call history will appear here.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            )
        }

        // Add more bento cards for actions, filters, etc. as needed
    }
}
