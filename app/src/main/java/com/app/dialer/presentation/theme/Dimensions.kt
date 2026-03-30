package com.app.dialer.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * Centralised dimension tokens for the Dialer app.
 *
 * All sizing constants in the UI layer must reference these tokens rather than
 * hardcoded dp values so that layout changes can be applied in a single place.
 */
object DialerDimens {
    /** Side length of a single keypad button cell. */
    val KeypadButtonSize = 72.dp

    /** Gap between adjacent keypad buttons. */
    val KeypadSpacing = 12.dp

    /** Diameter of the primary call FAB. */
    val CallButtonSize = 72.dp

    /** Small contact avatar (e.g. in suggestion list items). */
    val AvatarSizeSmall = 36.dp

    /** Medium contact avatar (e.g. in detail cards). */
    val AvatarSizeMedium = 48.dp

    /** Corner radius for bento-style cards and suggestion items. */
    val CardCornerRadius = 16.dp

    /** Height of the dialler number-input field area. */
    val InputFieldHeight = 80.dp

    /** Height of the persistent bottom action row (call / delete / voicemail). */
    val BottomBarHeight = 64.dp

    /** Gutter / gap between bento grid cells. */
    val BentoGutterSize = 12.dp
}
