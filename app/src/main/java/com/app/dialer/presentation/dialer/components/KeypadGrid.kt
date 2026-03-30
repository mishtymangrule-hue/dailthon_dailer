package com.app.dialer.presentation.dialer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.dialer.domain.model.DialerDigit
import com.app.dialer.presentation.theme.DialerDimens

/**
 * 3-column fixed keypad grid built from [DialerDigit.ALL].
 *
 * Each cell is square (aspect ratio 1:1). Long-press handlers are wired only
 * for the "0" (→ "+") and "*" (→ pause ",") keys.
 *
 * The grid is non-scrollable — all 12 keys always fit on screen.
 *
 * @param onDigitPressed      Called when a key is tapped. Passes the full [DialerDigit]
 *                            so the ViewModel can both append the digit and play DTMF.
 * @param onAsteriskLongPress Called on a long-press of the "*" key (insert pause ",").
 * @param onZeroLongPress     Called on a long-press of the "0" key (insert "+").
 * @param modifier            Modifier applied to the outer grid container.
 */
@Composable
fun KeypadGrid(
    onDigitPressed: (DialerDigit) -> Unit,
    onAsteriskLongPress: () -> Unit,
    onZeroLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(DialerDimens.KeypadSpacing),
        verticalArrangement = Arrangement.spacedBy(DialerDimens.KeypadSpacing),
        userScrollEnabled = false
    ) {
        items(
            items = DialerDigit.ALL,
            key = { it.digit }
        ) { dialerDigit ->
            KeypadButton(
                digit = dialerDigit,
                onClick = { onDigitPressed(dialerDigit) },
                onLongClick = when (dialerDigit.digit) {
                    "0" -> onZeroLongPress
                    "*" -> onAsteriskLongPress
                    else -> null
                },
                modifier = Modifier.aspectRatio(1f)
            )
        }
    }
}
