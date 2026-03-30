package com.app.dialer.presentation.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom elevation tokens for the Dialer app.
 * Each level corresponds to a Material3 elevation tier with specific shadow
 * properties for neumorphic rendering.
 */
object Elevation {
    /** Flat surface — no elevation, no shadow */
    val Level0: Dp = 0.dp

    /** Subtle lift — used for cards at rest */
    val Level1: Dp = 1.dp

    /** Low elevation — used for FABs, bottom navigation */
    val Level2: Dp = 3.dp

    /** Medium elevation — used for dialogs, drawers */
    val Level3: Dp = 6.dp

    /** High elevation — used for modals, popovers */
    val Level4: Dp = 8.dp

    /** Highest elevation — used for snackbars, tooltips */
    val Level5: Dp = 12.dp
}

/**
 * Neumorphic shadow descriptor.
 * [lightOffset] is the Dp distance for the highlight (top-left) shadow.
 * [darkOffset] is the Dp distance for the depth (bottom-right) shadow.
 * [blurRadius] controls shadow softness.
 */
data class NeumorphicShadow(
    val lightOffset: Dp,
    val darkOffset: Dp,
    val blurRadius: Dp
)

/**
 * Predefined neumorphic shadow levels aligned with elevation tokens.
 */
object NeumorphicElevation {
    val Level0 = NeumorphicShadow(lightOffset = 0.dp, darkOffset = 0.dp, blurRadius = 0.dp)
    val Level1 = NeumorphicShadow(lightOffset = 2.dp, darkOffset = 2.dp, blurRadius = 4.dp)
    val Level2 = NeumorphicShadow(lightOffset = 4.dp, darkOffset = 4.dp, blurRadius = 8.dp)
    val Level3 = NeumorphicShadow(lightOffset = 6.dp, darkOffset = 6.dp, blurRadius = 12.dp)
    val Level4 = NeumorphicShadow(lightOffset = 8.dp, darkOffset = 8.dp, blurRadius = 16.dp)
    val Level5 = NeumorphicShadow(lightOffset = 10.dp, darkOffset = 10.dp, blurRadius = 20.dp)
}
