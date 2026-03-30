package com.app.dialer.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.util.Log
import com.app.dialer.core.telecom.AudioRoute
import com.app.dialer.core.telecom.CallEvent
import com.app.dialer.core.telecom.CallEventBus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages audio routing and DTMF tone generation for in-call audio.
 *
 * ### Audio routing
 * Wraps [AudioManager] mode, speakerphone, and Bluetooth SCO controls behind a
 * single [setRoute] API. Route changes are published on [CallEventBus] so that
 * the in-call UI can reflect the current output device without polling.
 *
 * ### DTMF tones
 * [playDtmfTone] creates a [ToneGenerator] on [Dispatchers.IO], plays the tone
 * for 150 ms, then stops and releases it. A new generator is created per tone
 * invocation to avoid state issues on multi-press scenarios.
 *
 * ### Audio focus
 * Call [requestAudioFocus] when a call becomes active and [abandonAudioFocus]
 * when it ends. Uses the API-26-compatible [AudioFocusRequest.Builder] path on
 * API 26+.
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

    private var audioFocusRequest: AudioFocusRequest? = null
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "AudioRouteManager"
        private const val DTMF_TONE_DURATION_MS = 150L
        private const val DTMF_VOLUME = 80
    }

    // ─── Routing ──────────────────────────────────────────────────────────────

    /**
     * Applies [route] to [AudioManager] and emits a [CallEvent.AudioRouteChanged]
     * on [CallEventBus].
     *
     * Must be called from any thread; [AudioManager] calls are thread-safe.
     */
    fun setRoute(route: AudioRoute) {
        try {
            when (route) {
                AudioRoute.EARPIECE -> {
                    audioManager.isSpeakerphoneOn = false
                    audioManager.isBluetoothScoOn = false
                    audioManager.mode = AudioManager.MODE_IN_CALL
                }
                AudioRoute.SPEAKER -> {
                    audioManager.isSpeakerphoneOn = true
                    audioManager.isBluetoothScoOn = false
                    audioManager.mode = AudioManager.MODE_IN_CALL
                }
                AudioRoute.BLUETOOTH -> {
                    audioManager.startBluetoothSco()
                    audioManager.isBluetoothScoOn = true
                    audioManager.isSpeakerphoneOn = false
                    audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                }
                AudioRoute.WIRED_HEADSET -> {
                    audioManager.isSpeakerphoneOn = false
                    audioManager.isBluetoothScoOn = false
                    audioManager.mode = AudioManager.MODE_IN_CALL
                }
            }
            _currentRoute.value = route
            eventBus.emitSync(CallEvent.AudioRouteChanged(route))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set audio route to $route", e)
        }
    }

    /**
     * Returns all audio output routes currently available on the device.
     *
     * Always includes [AudioRoute.EARPIECE] and [AudioRoute.SPEAKER].
     * [AudioRoute.WIRED_HEADSET] is included when a headset is plugged in.
     * [AudioRoute.BLUETOOTH] is included when an off-call SCO device is available.
     */
    fun getAvailableRoutes(): List<AudioRoute> = buildList {
        add(AudioRoute.EARPIECE)
        add(AudioRoute.SPEAKER)
        if (audioManager.isWiredHeadsetOn) add(AudioRoute.WIRED_HEADSET)
        if (audioManager.isBluetoothScoAvailableOffCall) add(AudioRoute.BLUETOOTH)
    }

    // ─── DTMF ─────────────────────────────────────────────────────────────────

    /**
     * Plays a DTMF tone for [digit] on [Dispatchers.IO].
     *
     * Creates a fresh [ToneGenerator] per invocation (volume 80/100 on
     * [AudioManager.STREAM_DTMF]), plays for 150 ms, then stops and releases.
     * Silently no-ops for unmapped characters.
     *
     * @param digit The keypad character (0–9, *, #).
     */
    fun playDtmfTone(digit: Char) {
        val toneType = digitToTone(digit) ?: return
        ioScope.launch {
            var toneGenerator: ToneGenerator? = null
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_DTMF, DTMF_VOLUME)
                toneGenerator.startTone(toneType)
                delay(DTMF_TONE_DURATION_MS)
                toneGenerator.stopTone()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to play DTMF tone for '$digit'", e)
            } finally {
                toneGenerator?.release()
            }
        }
    }

    // ─── Audio focus ──────────────────────────────────────────────────────────

    /**
     * Requests transient audio focus for voice communication.
     *
     * Uses the [AudioFocusRequest.Builder] API (API 26+) with
     * [AudioManager.AUDIOFOCUS_GAIN_TRANSIENT] and
     * [AudioAttributes.USAGE_VOICE_COMMUNICATION].
     *
     * @return true when audio focus was granted immediately.
     */
    fun requestAudioFocus(): Boolean {
        return try {
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener { /* handled by CallStateManager */ }
                    .build()
                    .also { audioFocusRequest = it }
                audioManager.requestAudioFocus(request)
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    null,
                    AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                )
            }
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } catch (e: Exception) {
            Log.w(TAG, "Failed to request audio focus", e)
            false
        }
    }

    /**
     * Releases audio focus obtained by [requestAudioFocus].
     * Safe to call when focus was never requested.
     */
    fun abandonAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
                audioFocusRequest = null
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(null)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to abandon audio focus", e)
        }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private fun digitToTone(digit: Char): Int? = when (digit) {
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
        else -> null
    }
}
