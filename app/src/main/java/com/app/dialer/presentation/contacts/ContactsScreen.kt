package com.app.dialer.presentation.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.dialer.presentation.theme.neumorphicSurface

/**
 * Contacts screen placeholder — full implementation in Prompt 2.
 *
 * @param onContactClick  Called with a dialable phone number when the user taps a contact.
 */
@Composable
fun ContactsScreen(
    onContactClick: (String) -> Unit = {}
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
                text = "Contacts",
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
                text = "No contacts yet. Your saved contacts will appear here.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            )
        }

        // Add more bento cards for actions, favorites, etc. as needed
    }
}
