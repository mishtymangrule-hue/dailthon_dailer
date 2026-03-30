package com.app.dialer.core.audio

import android.content.Context
import android.util.Log
import com.app.dialer.core.telecom.AudioRoute
import com.app.dialer.core.telecom.CallEventBus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages audio routing and DTMF tone generation for in-call audio.
 *
 * ### P1 scope
 * Scaffold stub — all methods are no-ops that log intent only. Full
 * [android.media.AudioManager] integration (speakerphone, Bluetooth SCO,
 * audio focus), DTMF tone generation via [android.media.ToneGenerator],
 * and route change events on [CallEventBus] are implemented in Prompt 2
 * (InCallService / Audio module).
 */
@Singleton
class AudioRouteManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventBus: CallEventBus
) {
    private val _currentRoute = MutableStateFlow(AudioRoute.EARPIECE)

    /** The active audio output route. Defaults to [AudioRoute.EARPIECE] at startup. */
    val currentRoute: StateFlow<AudioRoute> = _currentRoute.asStateFlow()

    companion object {
        private const val TAG = "AudioRouteManager"
    }

    /** P2+: apply [route] via AudioManager. No-op stub for P1. */
    fun setRoute(route: AudioRoute) {
        Log.d(TAG, "setRoute($route) — P1 stub, no-op")
        _currentRoute.value = route
    }

    /** P2+: query AudioManager output devices. Returns earpiece + speaker for P1. */
    fun getAvailableRoutes(): List<AudioRoute> =
        listOf(AudioRoute.EARPIECE, AudioRoute.SPEAKER)

    /** P2+: play DTMF tone via ToneGenerator on IO thread. No-op stub for P1. */
    fun playDtmfTone(digit: Char) {
        Log.d(TAG, "playDtmfTone('$digit') — P1 stub, no-op")
    }

    /** P2+: request [android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT]. No-op for P1. */
    fun requestAudioFocus(): Boolean {
        Log.d(TAG, "requestAudioFocus — P1 stub")
        return true
    }

    /** P2+: abandon audio focus obtained by [requestAudioFocus]. No-op for P1. */
    fun abandonAudioFocus() {
        Log.d(TAG, "abandonAudioFocus — P1 stub")
    }
}

