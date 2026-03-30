package com.app.dialer.presentation.dialer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dialer.R
import com.app.dialer.presentation.theme.ElectricBlue
import com.app.dialer.presentation.theme.neumorphicSurface

/**
 * Dialer / keypad screen — placeholder shell for Prompt 1.
 *
 * Contains a functional digit display and keypad grid so layout composition
 * can be verified. Full dial / call logic will be wired in a subsequent prompt.
 *
 * @param onNavigateToCall  Called with a phone number string when the user
 *                          presses the call button. No-op at this stage.
 */
@Composable
fun DialerScreen(
    onNavigateToCall: (String) -> Unit = {}
) {
    var dialedNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ── App logo header ───────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.in_app_logo),
                contentDescription = "Dailathon logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Dailathon",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Every Call Matters",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Number display ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dialedNumber,
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 36.sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                if (dialedNumber.isNotEmpty()) {
                    IconButton(
                        onClick = { dialedNumber = dialedNumber.dropLast(1) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Backspace,
                            contentDescription = "Delete digit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Reserve space so number text stays centred when backspace is hidden
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Keypad grid ───────────────────────────────────────────────────
        KeypadGrid(
            onDigitEntered = { digit ->
                if (dialedNumber.length < 15) {
                    dialedNumber += digit
                }
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── Call button ───────────────────────────────────────────────────
        FloatingActionButton(
            onClick = {
                if (dialedNumber.isNotEmpty()) {
                    onNavigateToCall(dialedNumber)
                }
            },
            containerColor = ElectricBlue,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Call,
                contentDescription = "Call",
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun KeypadGrid(
    onDigitEntered: (String) -> Unit
) {
    val keys = listOf(
        listOf("1" to null, "2" to "ABC", "3" to "DEF"),
        listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
        listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
        listOf("*" to null, "0" to "+", "#" to null)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { (digit, letters) ->
                    KeypadButton(
                        digit = digit,
                        letters = letters,
                        onClick = { onDigitEntered(digit) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    digit: String,
    letters: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Track press state via InteractionSource so neumorphic elevation responds correctly
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .height(64.dp)
            // neumorphicSurface MUST precede clip so its drawBehind shadows are rendered
            // outside the composable's bounds before the GraphicsLayer clip is applied.
            // Placing clip() first would confine the shadow to the button's own area.
            .neumorphicSurface(
                cornerRadius = 16.dp,
                elevation = if (isPressed) 2.dp else 6.dp,
                isPressed = isPressed
            )
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = digit,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (letters != null) {
                Text(
                    text = letters,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
