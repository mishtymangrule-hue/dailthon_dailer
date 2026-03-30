package com.app.dialer.presentation.dialer.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.dialer.domain.model.DialerDigit
import com.app.dialer.presentation.theme.DialerAnimations
import com.app.dialer.presentation.theme.neumorphicSurface

/**
 * A single neumorphic keypad button that displays a [DialerDigit].
 *
 * Press state drives both the neumorphic shadow inversion and a spring-based
 * scale animation to reinforce the physical button metaphor. Haptic feedback
 * is delivered through the Compose [LocalHapticFeedback] API.
 *
 * Ripple is intentionally disabled — the neumorphic shadow depth change serves
 * as the visual affordance for press state.
 *
 * @param digit       The keypad data to render (label + sub-letters).
 * @param onClick     Invoked on a standard tap. Caller is responsible for
 *                    appending the digit and triggering DTMF playback.
 * @param onLongClick Optional callback for long-press (e.g. 0 → "+", * → ",").
 * @param modifier    Additional modifiers applied to the outer container.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeypadButton(
    digit: DialerDigit,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Spring-based scale: slightly shrinks on press, bounces back on release.
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = DialerAnimations.keypadButtonPress,
        label = "keypadScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .scale(scale)
            .neumorphicSurface(
                cornerRadius = 50.dp,   // fully circular
                elevation = if (isPressed) 2.dp else 6.dp,
                isPressed = isPressed
            )
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surface)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,       // neumorphic shadow replaces ripple
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
                onLongClick = if (onLongClick != null) {
                    {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
                } else null
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = digit.digit,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            // Sub-letters (e.g. "ABC") — hidden for *, # and 1
            if (digit.letters.isNotEmpty()) {
                Text(
                    text = digit.letters,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
    }
}
