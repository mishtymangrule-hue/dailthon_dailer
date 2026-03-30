package com.app.dialer.presentation.dialer.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.dialer.domain.model.SuggestedContact
import com.app.dialer.presentation.theme.DialerDimens
import com.app.dialer.presentation.theme.ElectricBlue
import com.app.dialer.presentation.theme.neumorphicSurface

/**
 * A single suggested-contact card in the dial-pad suggestion list.
 *
 * Layout: [ContactAvatar] | display name + phone number | direct-call icon button
 *
 * Animates in with a horizontal slide + fade on first composition.
 *
 * @param contact      The suggested contact to display.
 * @param onClick      Called when the row is tapped (fills the input field).
 * @param onCallDirect Called when the call icon is tapped (initiates call immediately).
 */
@Composable
fun SuggestedContactCard(
    contact: SuggestedContact,
    onClick: () -> Unit,
    onCallDirect: () -> Unit
) {
    // Drive the entry animation: start invisible, become visible after first frame
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)) + slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth / 3 },
            animationSpec = tween(200)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .neumorphicSurface(
                    cornerRadius = DialerDimens.CardCornerRadius,
                    elevation = 4.dp
                )
                .clip(RoundedCornerShape(DialerDimens.CardCornerRadius))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            ContactAvatar(
                photoUri = contact.photoUri,
                displayName = contact.displayName,
                size = DialerDimens.AvatarSizeSmall
            )

            Spacer(modifier = Modifier.width(12.dp))

            androidx.compose.foundation.layout.Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Direct-call shortcut
            IconButton(
                onClick = onCallDirect,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Call,
                    contentDescription = "Call ${contact.displayName}",
                    tint = ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
