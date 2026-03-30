package com.app.dialer.presentation.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
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

    drawBehind {
        val elevationPx = elevation.toPx()
        val cornerRadiusPx = cornerRadius.toPx()
        val blurRadiusPx = elevationPx * 2f

        drawIntoCanvas { canvas ->
            // Light (highlight) shadow — upper-left
            val lightPaint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(
                        blurRadiusPx,
                        if (isPressed) elevationPx else -elevationPx,
                        if (isPressed) elevationPx else -elevationPx,
                        resolvedLightShadow.toArgb()
                    )
                }
            }

            // Dark (depth) shadow — lower-right
            val darkPaint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(
                        blurRadiusPx,
                        if (isPressed) -elevationPx else elevationPx,
                        if (isPressed) -elevationPx else elevationPx,
                        resolvedDarkShadow.toArgb()
                    )
                }
            }

            val left = 0f
            val top = 0f
            val right = size.width
            val bottom = size.height

            // Draw light shadow pass
            canvas.drawRoundRect(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                radiusX = cornerRadiusPx,
                radiusY = cornerRadiusPx,
                paint = lightPaint
            )

            // Draw dark shadow pass
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
