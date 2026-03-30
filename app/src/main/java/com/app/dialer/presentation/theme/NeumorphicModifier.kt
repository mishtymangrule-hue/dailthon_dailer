package com.app.dialer.presentation.theme

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a neumorphic surface effect using dual-shadow rendering.
 *
 * In "raised" mode (isPressed = false), two drop-shadows are rendered:
 *  - A light highlight shadow offset toward the upper-left
 *  - A dark depth shadow offset toward the lower-right
 *
 * In "pressed" mode (isPressed = true), the shadows are inverted to simulate
 * an inset / inner-pressed state.
 *
 * ### Rendering notes
 * Compose's [drawBehind] records drawing commands via Skia's hardware RenderNode
 * pipeline, which **does** honour [setShadowLayer] for non-text shapes — unlike
 * the legacy Android View hardware-DisplayList pipeline (which ignores it for
 * shapes on API 28+). Shadow rendering therefore works correctly on physical
 * devices and hardware-accelerated emulators.
 *
 * Screenshot / unit-test frameworks that force a pure-software Skia rasteriser
 * (e.g. Paparazzi, Robolectric with software rendering) bypass the RenderNode
 * path and will not produce the shadow layers. Verify shadow appearance on a
 * real device or hardware-accelerated emulator, not via software-rendered tests.
 *
 * @param cornerRadius     Corner radius of the neumorphic surface.
 * @param lightShadowColor Override for the highlight shadow color. Defaults to
 *                         [LocalNeumorphicColors.shadowLight].
 * @param darkShadowColor  Override for the depth shadow color. Defaults to
 *                         [LocalNeumorphicColors.shadowDark].
 * @param elevation        Controls shadow offset distance and blur radius.
 * @param isPressed        When true, renders inverted shadows for the pressed state.
 */
fun Modifier.neumorphicSurface(
    cornerRadius: Dp = 16.dp,
    lightShadowColor: Color? = null,
    darkShadowColor: Color? = null,
    elevation: Dp = 6.dp,
    isPressed: Boolean = false
): Modifier = composed {
    val neumorphicColors = LocalNeumorphicColors.current
    val resolvedLightShadow = lightShadowColor ?: neumorphicColors.shadowLight
    val resolvedDarkShadow = darkShadowColor ?: neumorphicColors.shadowDark

    // Compute pixel values here (composed scope) so they are not re-derived per draw frame.
    val density = LocalDensity.current
    val elevationPx = with(density) { elevation.toPx() }
    val cornerRadiusPx = with(density) { cornerRadius.toPx() }
    val blurRadiusPx = elevationPx * 2f

    // Negative offset → upper-left (light source); positive → lower-right (depth).
    // When pressed the directions flip to simulate an inset state.
    val lightOffset = if (isPressed) elevationPx else -elevationPx
    val darkOffset = -lightOffset   // always the opposite direction

    // Remember Paint objects and only recreate them when the inputs that affect them change.
    // Allocating Paint inside drawBehind/drawIntoCanvas would create two new objects on every
    // draw frame — with 12 keypad buttons each holding this modifier that is 24 allocations
    // per frame, creating constant GC pressure during any UI animation.
    val lightPaint = remember(resolvedLightShadow, elevationPx, isPressed) {
        Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                color = android.graphics.Color.TRANSPARENT
                setShadowLayer(blurRadiusPx, lightOffset, lightOffset, resolvedLightShadow.toArgb())
            }
        }
    }

    val darkPaint = remember(resolvedDarkShadow, elevationPx, isPressed) {
        Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                color = android.graphics.Color.TRANSPARENT
                setShadowLayer(blurRadiusPx, darkOffset, darkOffset, resolvedDarkShadow.toArgb())
            }
        }
    }

    drawBehind {
        drawIntoCanvas { canvas ->
            val left = 0f
            val top = 0f
            val right = size.width
            val bottom = size.height

            // Light (highlight) shadow pass — upper-left
            canvas.drawRoundRect(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx,
                paint = lightPaint
            )

            // Dark (depth) shadow pass — lower-right
            canvas.drawRoundRect(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx,
                paint = darkPaint
            )
        }
    }
}
