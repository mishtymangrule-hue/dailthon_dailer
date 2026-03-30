package com.app.dialer.presentation.dialer.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.dialer.domain.model.SuggestedContact

/**
 * Scrollable list of up to 5 suggested contacts shown while the user types.
 *
 * - Items are staggered: each successive item's entry animation is delayed by
 *   [index] * 30 ms via [SuggestedContactCard]'s [LaunchedEffect].
 * - A 0.5 dp divider separates adjacent items.
 * - The empty-state is not rendered here — the caller wraps this composable
 *   in [AnimatedVisibility] and hides it when the list is empty.
 *
 * @param contacts          Contacts to display (caller passes at most 5).
 * @param onContactSelected Called when the user taps a row — fills the dial-pad.
 * @param onContactCallDirect Called when the user taps the call icon — direct call.
 */
@Composable
fun SuggestedContactsList(
    contacts: List<SuggestedContact>,
    onContactSelected: (SuggestedContact) -> Unit,
    onContactCallDirect: (SuggestedContact) -> Unit,
    modifier: Modifier = Modifier
) {
    // Limit display to 5
    val visibleContacts = contacts.take(5)

    LazyColumn(modifier = modifier.fillMaxWidth()) {
        itemsIndexed(
            items = visibleContacts,
            key = { _, contact -> contact.id }
        ) { index, contact ->
            SuggestedContactCard(
                contact = contact,
                onClick = { onContactSelected(contact) },
                onCallDirect = { onContactCallDirect(contact) }
            )

            // Divider between items (not after the last one)
            if (index < visibleContacts.lastIndex) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 0.5.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )
            }
        }
    }
}
