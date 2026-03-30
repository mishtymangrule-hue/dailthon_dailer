package com.app.dialer.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Holds the neumorphic shadow colors for the current theme.
 * Injected via [LocalNeumorphicColors] composition local.
 */
data class NeumorphicColors(
    /** Highlight shadow color — upper-left light source */
    val shadowLight: Color,
    /** Depth shadow color — lower-right dark source */
    val shadowDark: Color,
    /** Background surface color used as the base for neumorphic effect */
    val surfaceColor: Color
)

val DarkNeumorphicColors = NeumorphicColors(
    shadowLight = NeumorphicShadowLightDark,
    shadowDark = NeumorphicShadowDarkDark,
    surfaceColor = DarkSurface
)

val LightNeumorphicColors = NeumorphicColors(
    shadowLight = NeumorphicShadowLightLight,
    shadowDark = NeumorphicShadowDarkLight,
    surfaceColor = LightSurface
)

val LocalNeumorphicColors = staticCompositionLocalOf { LightNeumorphicColors }
