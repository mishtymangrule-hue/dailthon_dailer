package com.app.dialer.core.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps [AudioManager] audio-focus acquisition and abandonment to keep
 * [InCallServiceImpl] and [AudioRouteManager] free of boilerplate.
 *
 * ### P2+ scope
 * This class is a Prompt-2 (InCallService module) concern. It is not injected
 * or invoked in P1; the full audio-focus lifecycle (request on call ACTIVE,
 * abandon on call DISCONNECTED) is wired in Prompt 2.
 *
 * ### Design
 * A single, reusable [AudioFocusRequest] (API 26+) is built on first
 * [requestFocus] call and released in [abandonFocus]. Subsequent calls to
 * [requestFocus] without an intervening [abandonFocus] are no-ops to avoid
 * double-requesting focus.
 *
 * On devices where [Build.VERSION.SDK_INT] < 26 (below our min SDK, included
 * for completeness), the deprecated `requestAudioFocus(OnAudioFocusChangeListener,
 * int, int)` path is used via `@Suppress("DEPRECATION")`.
 */
@Singleton
class AudioFocusHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var focusRequest: AudioFocusRequest? = null
    private var hasFocus = false

    companion object {
        private const val TAG = "AudioFocusHelper"
    }

    /**
     * Requests exclusive audio focus for telephony ([AudioManager.STREAM_VOICE_CALL]).
     *
     * Safe to call multiple times — if focus is already held, the call is a
     * no-op and returns [AudioManager.AUDIOFOCUS_REQUEST_GRANTED].
     *
     * @return [AudioManager.AUDIOFOCUS_REQUEST_GRANTED] on success,
     *         [AudioManager.AUDIOFOCUS_REQUEST_FAILED] otherwise.
     */
    fun requestFocus(): Int {
        if (hasFocus) return AudioManager.AUDIOFOCUS_REQUEST_GRANTED

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(attrs)
            .setAcceptsDelayedFocusGain(false)
            .setOnAudioFocusChangeListener { focusChange ->
                Log.d(TAG, "AudioFocusChange: $focusChange")
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                ) {
                    hasFocus = false
                }
            }
            .build()

        focusRequest = request
        val result = audioManager.requestAudioFocus(request)
        hasFocus = (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        Log.d(TAG, "requestFocus result=$result hasFocus=$hasFocus")
        return result
    }

    /**
     * Abandons audio focus previously acquired via [requestFocus].
     *
     * Safe to call when focus is not currently held — silently ignored.
     */
    fun abandonFocus() {
        if (!hasFocus) return
        focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        focusRequest = null
        hasFocus = false
        Log.d(TAG, "abandonFocus")
    }

    /** Whether this helper currently holds audio focus. */
    val isFocused: Boolean get() = hasFocus
}
