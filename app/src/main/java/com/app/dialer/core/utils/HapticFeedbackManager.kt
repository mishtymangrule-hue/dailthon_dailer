package com.app.dialer.core.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralised haptic feedback helper.
 *
 * Abstracts the API-level differences between:
 * - API 29+: [HapticFeedbackConstants] via [View.performHapticFeedback]
 * - API 26–28: [VibrationEffect] via [Vibrator]
 *
 * All methods silently swallow [SecurityException] in case the
 * [android.Manifest.permission.VIBRATE] permission is not declared.
 */
@Singleton
class HapticFeedbackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                    as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /**
     * Short tactile click — used for keypad digit presses.
     *
     * @param view Optional [View] reference. When provided and API >= 29,
     *             [View.performHapticFeedback] is preferred over the [Vibrator]
     *             path because it respects system-level haptic intensity settings.
     */
    fun performClick(view: View? = null) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && view != null) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            } else {
                vibrateLegacy(durationMs = 30L, amplitude = 80)
            }
        } catch (e: SecurityException) {
            // VIBRATE permission not granted — silently ignore
        }
    }

    /**
     * Long-press haptic — used for delete-all and zero→"+" long press.
     *
     * @param view Optional [View] for API 29+ path.
     */
    fun performLongPress(view: View? = null) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && view != null) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            } else {
                vibrateLegacy(durationMs = 60L, amplitude = 120)
            }
        } catch (e: SecurityException) {
            // VIBRATE permission not granted — silently ignore
        }
    }

    /**
     * Error haptic — used when a call cannot be initiated (e.g. invalid number).
     *
     * Produces a short double-pulse pattern to signal rejection.
     *
     * @param view Optional [View] for API 29+ path.
     */
    fun performError(view: View? = null) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && view != null) {
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
            } else {
                vibrateWaveformLegacy(
                    timings = longArrayOf(0L, 30L, 60L, 30L),
                    repeat = -1
                )
            }
        } catch (e: SecurityException) {
            // VIBRATE permission not granted — silently ignore
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private fun vibrateLegacy(durationMs: Long, amplitude: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(durationMs, amplitude)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun vibrateWaveformLegacy(timings: LongArray, repeat: Int) {
        vibrator.vibrate(VibrationEffect.createWaveform(timings, repeat))
    }
}
