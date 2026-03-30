package com.app.dialer.presentation.dialer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Voicemail
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.app.dialer.presentation.theme.ErrorRed
import com.app.dialer.presentation.theme.neumorphicSurface

/**
 * Small voicemail shortcut button positioned in the bottom-left of the action row.
 *
 * Displays a red dot badge when [hasUnread] is true to draw the user's attention
 * to waiting voicemail messages.
 *
 * @param onClick   Called when the button is tapped.
 * @param hasUnread Whether a red unread badge should be shown.
 */
@Composable
fun VoicemailButton(
    onClick: () -> Unit,
    hasUnread: Boolean,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    BadgedBox(
        badge = {
            if (hasUnread) {
                Badge(
                    containerColor = ErrorRed,
                    modifier = Modifier
                        .offset(x = (-4).dp, y = 4.dp)
                        .size(8.dp)
                )
            }
        },
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .neumorphicSurface(
                    cornerRadius = 50.dp,
                    elevation = if (isPressed) 2.dp else 4.dp,
                    isPressed = isPressed
                )
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    }
                )
        ) {
            Icon(
                imageVector = Icons.Rounded.Voicemail,
                contentDescription = "Voicemail",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
