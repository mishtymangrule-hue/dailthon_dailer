package com.app.dialer.presentation.dialer.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.app.dialer.presentation.theme.DialerDimens
import com.app.dialer.presentation.theme.ElectricBlue
import com.app.dialer.presentation.theme.SoftTeal
import com.app.dialer.presentation.theme.neumorphicSurface
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource

/**
 * Primary call FAB-style button.
 *
 * - Circular, 72 dp diameter.
 * - Background: linear gradient from [ElectricBlue] to [SoftTeal] at 135°.
 * - When [isEnabled], a subtle infinite scale pulse (1.0 → 1.06) draws attention.
 * - When disabled, renders flat at 40 % alpha with no animation.
 * - Neumorphic outer shadow is applied only when [isEnabled].
 *
 * @param onClick   Invoked when the button is tapped and [isEnabled] is true.
 * @param isEnabled Whether the button is interactive (input non-empty).
 * @param modifier  Additional modifiers.
 */
@Composable
fun CallButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // Scale pulse: oscillates 1.0 ↔ 1.06 while enabled; snaps back to 1.0 when disabled.
    val pulseScale = remember { Animatable(1f) }
    LaunchedEffect(isEnabled) {
        if (isEnabled) {
            while (true) {
                pulseScale.animateTo(
                    targetValue = 1.06f,
                    animationSpec = tween(durationMillis = 600)
                )
                pulseScale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 600)
                )
            }
        } else {
            pulseScale.snapTo(1f)
        }
    }

    val callGradient = Brush.linearGradient(
        colors = listOf(ElectricBlue, SoftTeal),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(100f, 100f)
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(DialerDimens.CallButtonSize)
            .scale(if (isEnabled) pulseScale.value else 1f)
            .then(
                if (isEnabled) {
                    Modifier.neumorphicSurface(
                        cornerRadius = 50.dp,
                        elevation = 8.dp,
                        isPressed = false
                    )
                } else Modifier
            )
            .clip(CircleShape)
            .background(brush = callGradient, alpha = if (isEnabled) 1f else 0.4f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
    ) {
        Icon(
            imageVector = Icons.Rounded.Call,
            contentDescription = "Call",
            tint = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}
