package com.app.dialer.presentation.dialer.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.app.dialer.presentation.theme.neumorphicSurface

/**
 * Backspace / delete button for the dialler input field.
 *
 * - Appears with a scale+fade animation when [isVisible] becomes true.
 * - Single tap: removes the last entered digit.
 * - Long press: clears the entire input field.
 * - Haptic feedback uses [HapticFeedbackType.TextHandleMove] for single tap
 *   and [HapticFeedbackType.LongPress] for the long-press action.
 *
 * @param onClick     Remove last character.
 * @param onLongClick Clear all input.
 * @param isVisible   Controls [AnimatedVisibility] — hide when input is empty.
 * @param modifier    Additional modifiers.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeleteButton(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(150)) + scaleIn(initialScale = 0.8f, animationSpec = tween(150)),
        exit = fadeOut(tween(120)) + scaleOut(targetScale = 0.8f, animationSpec = tween(120))
    ) {
        val haptic = LocalHapticFeedback.current
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(48.dp)
                .neumorphicSurface(
                    cornerRadius = 50.dp,
                    elevation = if (isPressed) 2.dp else 4.dp,
                    isPressed = isPressed
                )
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Backspace,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
