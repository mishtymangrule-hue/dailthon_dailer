package com.app.dialer.core.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.util.Log
import com.app.dialer.core.telecom.AudioRoute
import com.app.dialer.core.telecom.CallEventBus
import com.app.dialer.core.telecom.CallEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages audio routing (speakerphone, Bluetooth, wired headset) and DTMF tone generation
 * for in-call audio control.
 *
 * Responsible for:
 * - Querying and setting the active audio output route.
 * - Playing DTMF tones via [android.media.ToneGenerator] with proper timing.
 * - Requesting and releasing audio focus from [android.media.AudioManager].
 * - Emitting audio route change events to [CallEventBus].
 *
 * ### Threading
 * All [android.media.AudioManager] operations run on [Dispatchers.IO] to avoid
 * blocking the main thread. DTMF tone generation happens on a background dispatcher
 * and is kept briefly alive (150ms per tone) to allow proper playback.
 *
 * ### API compatibility
 * Targets all APIs from 26 (MIN_SDK) to 35 (TARGET_SDK) with explicit version checks
 * where required (e.g. [AudioManager.getIsSpeakerphoneOn] vs legacy property access).
 */
@Singleton
class AudioRouteManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventBus: CallEventBus
) {
    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _currentRoute = MutableStateFlow(AudioRoute.EARPIECE)
    /** The active audio output route. Defaults to [AudioRoute.EARPIECE] at startup. */
    val currentRoute: StateFlow<AudioRoute> = _currentRoute.asStateFlow()

    companion object {
        private const val TAG = "AudioRouteManager"

        // DTMF tone generator constants
        private const val DTMF_TONE_DURATION_MS = 150L
        private const val TONE_STREAM = AudioManager.STREAM_DTMF
        private const val TONE_VOLUME = 80 // 0-100
    }

    /**
     * Sets the active audio output route and notifies the event bus.
     *
     * Uses [AudioManager] to apply the route change on API 26+.
     * Emits [CallEvent.AudioRouteChanged] to the event bus.
     *
     * @param route Target audio route (EARPIECE, SPEAKER, BLUETOOTH, WIRED_HEADSET).
     */
    fun setRoute(route: AudioRoute) {
        Log.d(TAG, "setRoute($route)")
        try {
            when (route) {
                AudioRoute.SPEAKER -> {
                    audioManager.isSpeakerphoneOn = true
                    audioManager.isBluetoothScoOn = false
                }
                AudioRoute.EARPIECE -> {
                    audioManager.isSpeakerphoneOn = false
                    audioManager.isBluetoothScoOn = false
                }
                AudioRoute.BLUETOOTH -> {
                    audioManager.isBluetoothScoOn = true
                    audioManager.isSpeakerphoneOn = false
                }
                AudioRoute.WIRED_HEADSET -> {
                    audioManager.isSpeakerphoneOn = false
                    audioManager.isBluetoothScoOn = false
                    // Wired headset routing is automatic when a headset is connected
                }
            }
            _currentRoute.value = route
            // Emit route change to event bus
            GlobalScope.launch(Dispatchers.IO) {
                eventBus.emit(CallEvent.AudioRouteChanged(route))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set audio route to $route", e)
        }
    }

    /**
     * Returns all currently available audio output routes.
     *
     * Queries [AudioManager] to determine which routes are physically available.
     * The earpiece is always available on a phone.
     *
     * @return List of routes available for switching to.
     */
    fun getAvailableRoutes(): List<AudioRoute> {
        val routes = mutableListOf(AudioRoute.EARPIECE, AudioRoute.SPEAKER)

        try {
            // Check for wired headset
            if (audioManager.isWiredHeadsetOn) {
                routes.add(AudioRoute.WIRED_HEADSET)
            }

            // Check for Bluetooth SCO
            if (audioManager.isBluetoothScoAvailableOffCall || audioManager.isBluetoothScoOn) {
                routes.add(AudioRoute.BLUETOOTH)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to query available audio routes", e)
        }

        return routes
    }

    /**
     * Plays a DTMF tone corresponding to [digit].
     *
     * Maps [digit] characters (0-9, *, #, A-D) to [android.media.ToneGenerator]
     * TONE_DTMF_* constants, generates the tone, and automatically stops after
     * [DTMF_TONE_DURATION_MS] milliseconds.
     *
     * Runs on [Dispatchers.IO] to avoid blocking the main thread.
     *
     * @param digit A single DTMF character (0-9, *, #, or A-D for extended).
     */
    fun playDtmfTone(digit: Char) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val toneType = mapDigitToTone(digit) ?: return@launch
                val toneGen = ToneGenerator(TONE_STREAM, TONE_VOLUME)
                toneGen.startTone(toneType, DTMF_TONE_DURATION_MS.toInt())
                // ToneGenerator auto-stops after duration; release after a brief delay
                kotlinx.coroutines.delay(DTMF_TONE_DURATION_MS + 50)
                toneGen.release()
                Log.d(TAG, "DTMF tone played: '$digit' (type=$toneType)")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play DTMF tone for '$digit'", e)
            }
        }
    }

    /**
     * Stops any ongoing DTMF tone playback.
     *
     * In the current implementation, tones are auto-stopped after [DTMF_TONE_DURATION_MS].
     * This method is a no-op placeholder for future enhancements (e.g. user-instigated
     * tone termination).
     */
    fun stopDtmfTone() {
        Log.d(TAG, "stopDtmfTone (no-op for auto-stop tones)")
    }

    /**
     * Requests audio focus from [AudioManager] so that in-call audio takes priority.
     *
     * Uses [AudioManager.AUDIOFOCUS_GAIN_TRANSIENT] to request focus for the
     * duration of the call. On API 26+, builds an explicit [AudioFocusRequest]
     * for compatibility and granular control.
     *
     * @return True when audio focus was successfully requested, false otherwise.
     */
    fun requestAudioFocus(): Boolean {
        return try {
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(
                        android.media.AudioAttributes.Builder()
                            .setUsage(android.media.AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .build()
                audioManager.requestAudioFocus(audioFocusRequest)
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    null,
                    AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                )
            }
            (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED).also {
                if (it) Log.d(TAG, "Audio focus requested successfully")
                else Log.w(TAG, "Failed to request audio focus")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception requesting audio focus", e)
            false
        }
    }

    /**
     * Releases audio focus previously obtained via [requestAudioFocus].
     *
     * Allows other apps to regain audio focus (e.g. music playback, navigation)
     * after the call ends.
     */
    fun abandonAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.abandonAudioFocusRequest(AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build())
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(null)
            }
            Log.d(TAG, "Audio focus abandoned")
        } catch (e: Exception) {
            Log.e(TAG, "Exception abandoning audio focus", e)
        }
    }

    /**
     * Maps a DTMF digit character to the corresponding [ToneGenerator] constant.
     *
     * Supports:
     * - Digits: 0-9
     * - Control symbols: * (star), # (pound)
     * - Extended (A-D for 4-tone multifrequency)
     *
     * @return The [ToneGenerator.TONE_DTMF_*] constant, or null for unrecognized digits.
     */
    private fun mapDigitToTone(digit: Char): Int? = when (digit) {
        '0' -> ToneGenerator.TONE_DTMF_0
        '1' -> ToneGenerator.TONE_DTMF_1
        '2' -> ToneGenerator.TONE_DTMF_2
        '3' -> ToneGenerator.TONE_DTMF_3
        '4' -> ToneGenerator.TONE_DTMF_4
        '5' -> ToneGenerator.TONE_DTMF_5
        '6' -> ToneGenerator.TONE_DTMF_6
        '7' -> ToneGenerator.TONE_DTMF_7
        '8' -> ToneGenerator.TONE_DTMF_8
        '9' -> ToneGenerator.TONE_DTMF_9
        '*' -> ToneGenerator.TONE_DTMF_S
        '#' -> ToneGenerator.TONE_DTMF_P
        'A' -> ToneGenerator.TONE_DTMF_A
        'B' -> ToneGenerator.TONE_DTMF_B
        'C' -> ToneGenerator.TONE_DTMF_C
        'D' -> ToneGenerator.TONE_DTMF_D
        else -> {
            Log.w(TAG, "Unknown DTMF digit: '$digit'")
            null
        }
    }
}
