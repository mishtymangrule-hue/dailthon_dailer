package com.app.dialer.presentation.dialer.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dialer.presentation.dialer.DialerInputState
import com.app.dialer.presentation.theme.DialerDimens
import com.app.dialer.presentation.theme.ElectricBlue
import kotlinx.coroutines.delay

/**
 * Custom read-only phone number display field.
 *
 * Renders the formatted phone number using [MaterialTheme.typography.displayMedium]
 * with a blinking accent-colour cursor. When the input is empty a muted hint is
 * shown instead. The field scrolls horizontally when the number text overflows.
 *
 * Long-pressing the field copies the current raw input to the system clipboard
 * and triggers [onPasteRequest] so the ViewModel can sanitise and re-set it.
 *
 * **No actual [androidx.compose.material3.TextField] is used** — the field is built
 * entirely from [Text] composables so we have full control over styling.
 *
 * @param state          Current input state from the ViewModel.
 * @param onPasteRequest Called on long-press with the clipboard string so the
 *                       ViewModel can call `onPasteInput()`.
 * @param modifier       Additional modifiers.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialerInputField(
    state: DialerInputState,
    onPasteRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard: ClipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()
    val interactionSource = remember { MutableInteractionSource() }

    // Auto-scroll to end whenever the text changes
    LaunchedEffect(state.formattedInput) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    // Blinking cursor — alternates visibility every 530ms
    var cursorVisible by remember { mutableStateOf(true) }
    LaunchedEffect(state.rawInput) {
        // Reset blink phase on each keystroke so cursor stays solid while typing
        cursorVisible = true
        while (true) {
            delay(530L)
            cursorVisible = !cursorVisible
        }
    }

    // Country flag emoji derived from leading "+XX" country code (best-effort, IN default)
    val flagEmoji = remember(state.rawInput) {
        countryFlagFromNumber(state.rawInput)
    }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .fillMaxWidth()
            .height(DialerDimens.InputFieldHeight)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {},
                onLongClick = {
                    // On long press: paste from clipboard into dialler
                    val clipText = clipboard.getText()?.text
                    if (!clipText.isNullOrBlank()) {
                        onPasteRequest(clipText)
                    }
                }
            )
    ) {
        if (state.rawInput.isEmpty()) {
            // Hint text
            Text(
                text = "Enter number",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .horizontalScroll(scrollState)
                    .wrapContentWidth()
            ) {
                // Country flag prefix
                if (flagEmoji.isNotEmpty()) {
                    Text(
                        text = flagEmoji,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                // Animated phone number — slides in from right on each digit change
                AnimatedContent(
                    targetState = state.formattedInput,
                    transitionSpec = {
                        (slideInHorizontally(
                            initialOffsetX = { width -> width / 4 },
                            animationSpec = tween(80)
                        ) + fadeIn(tween(80))) togetherWith fadeOut(tween(40))
                    },
                    label = "inputNumber"
                ) { displayText ->
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Light
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1
                    )
                }

                // Blinking cursor
                Spacer(modifier = Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(36.dp)
                        .background(
                            color = if (cursorVisible) ElectricBlue
                            else ElectricBlue.copy(alpha = 0f)
                        )
                )
            }
        }
    }
}

/**
 * Best-effort country flag emoji lookup from a phone number string.
 *
 * Checks for a leading "+" and maps the first 1-3 digits to a known country code.
 * Returns an empty string when no match is found (number typed without country prefix).
 */
private fun countryFlagFromNumber(number: String): String {
    if (!number.startsWith("+")) return ""
    val digits = number.drop(1).filter { it.isDigit() }
    // Check 3-digit prefixes first, then 2-digit, then 1-digit
    return countryCodeToFlag(digits.take(3))
        ?: countryCodeToFlag(digits.take(2))
        ?: countryCodeToFlag(digits.take(1))
        ?: ""
}

private fun countryCodeToFlag(code: String): String? {
    val country = when (code) {
        "91"  -> "IN"
        "1"   -> "US"
        "44"  -> "GB"
        "49"  -> "DE"
        "33"  -> "FR"
        "81"  -> "JP"
        "86"  -> "CN"
        "61"  -> "AU"
        "55"  -> "BR"
        "7"   -> "RU"
        "39"  -> "IT"
        "34"  -> "ES"
        "82"  -> "KR"
        "62"  -> "ID"
        "52"  -> "MX"
        "31"  -> "NL"
        "966" -> "SA"
        "971" -> "AE"
        "92"  -> "PK"
        "880" -> "BD"
        "65"  -> "SG"
        "60"  -> "MY"
        "63"  -> "PH"
        "66"  -> "TH"
        "84"  -> "VN"
        "27"  -> "ZA"
        "20"  -> "EG"
        "234" -> "NG"
        "254" -> "KE"
        else  -> null
    } ?: return null

    // Convert ISO-3166 alpha-2 country code to flag emoji using regional indicator symbols
    val base = 0x1F1E6 - 'A'.code
    return country.map { char -> String(Character.toChars(base + char.code)) }.joinToString("")
}
