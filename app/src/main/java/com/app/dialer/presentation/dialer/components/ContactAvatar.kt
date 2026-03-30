package com.app.dialer.presentation.dialer.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.app.dialer.presentation.theme.ElectricBlue
import com.app.dialer.presentation.theme.SoftTeal

/**
 * Circular contact avatar.
 *
 * - If [photoUri] is non-null, loads the image via Coil [SubcomposeAsyncImage]
 *   with a circular clip. On load error, falls back to the initials view.
 * - If [photoUri] is null (or the image fails to load), renders a circle with
 *   the brand gradient background and up to 2 initials characters in white.
 *
 * @param photoUri    Optional contact photo URI.
 * @param displayName Contact display name, used to derive initials.
 * @param size        Diameter of the avatar circle. Default 40 dp.
 */
@Composable
fun ContactAvatar(
    photoUri: Uri?,
    displayName: String,
    size: Dp = 40.dp
) {
    val initials = remember(displayName) { extractInitials(displayName) }

    val avatarGradient = Brush.linearGradient(
        colors = listOf(ElectricBlue, SoftTeal)
    )

    if (photoUri != null) {
        SubcomposeAsyncImage(
            model = photoUri,
            contentDescription = "Avatar for $displayName",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            error = {
                // Coil error → fall back to initials
                InitialsAvatar(
                    initials = initials,
                    gradient = avatarGradient,
                    size = size
                )
            },
            loading = {
                // Show initials as placeholder while loading
                InitialsAvatar(
                    initials = initials,
                    gradient = avatarGradient,
                    size = size
                )
            }
        )
    } else {
        InitialsAvatar(
            initials = initials,
            gradient = avatarGradient,
            size = size
        )
    }
}

@Composable
private fun InitialsAvatar(
    initials: String,
    gradient: Brush,
    size: Dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(gradient)
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.35f).sp
            ),
            color = Color.White
        )
    }
}

/** Extracts up to 2 uppercase initial characters from a display name. */
private fun extractInitials(name: String): String {
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words[0].take(2).uppercase()
        else -> "${words[0].first().uppercaseChar()}${words[1].first().uppercaseChar()}"
    }
}
