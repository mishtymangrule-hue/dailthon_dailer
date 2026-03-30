package com.app.dialer.presentation.dialer.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SimCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.dialer.domain.model.SimCard
import com.app.dialer.presentation.theme.ElectricBlue

/**
 * Bottom-sheet style alert dialog for SIM card selection.
 *
 * Shows each [SimCard] in a row with:
 * - A SIM card icon
 * - Slot-index badge ("SIM 1" / "SIM 2")
 * - Carrier name
 * - Accent border when this card is the system default
 *
 * @param sims          Available SIM cards (typically 1 or 2).
 * @param onSimSelected Called with the chosen [SimCard] when a row is tapped.
 * @param onDismiss     Called when the Cancel button is pressed.
 */
@Composable
fun SimSelectorDialog(
    sims: List<SimCard>,
    onSimSelected: (SimCard) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose SIM card",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sims.forEach { sim ->
                    SimRow(
                        sim = sim,
                        onClick = { onSimSelected(sim) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SimRow(
    sim: SimCard,
    onClick: () -> Unit
) {
    val borderModifier = if (sim.isDefault) {
        Modifier.border(
            width = 1.5.dp,
            color = ElectricBlue,
            shape = RoundedCornerShape(12.dp)
        )
    } else Modifier

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(borderModifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.SimCard,
            contentDescription = null,
            tint = if (sim.isDefault) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = sim.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = sim.carrierName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Slot badge
        Badge(
            containerColor = if (sim.isDefault)
                ElectricBlue else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (sim.isDefault)
                MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Text(
                text = "SIM ${sim.slotIndex + 1}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
