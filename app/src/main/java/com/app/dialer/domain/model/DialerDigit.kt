package com.app.dialer.domain.model

import android.media.ToneGenerator

/**
 * Represents a single key on the phone keypad.
 *
 * @param digit     The digit or symbol string shown on the key (e.g. "0", "*", "#").
 * @param letters   Secondary label shown below the digit (e.g. "ABC"). Empty for 0, 1, *, #.
 * @param dtmfTone  The [ToneGenerator] TONE_DTMF_* constant to play when this key is pressed.
 */
data class DialerDigit(
    val digit: String,
    val letters: String,
    val dtmfTone: Int
) {
    companion object {
        /** All 12 keypad entries in standard dial-pad order. */
        val ALL: List<DialerDigit> = listOf(
            DialerDigit(digit = "1", letters = "",     dtmfTone = ToneGenerator.TONE_DTMF_1),
            DialerDigit(digit = "2", letters = "ABC",  dtmfTone = ToneGenerator.TONE_DTMF_2),
            DialerDigit(digit = "3", letters = "DEF",  dtmfTone = ToneGenerator.TONE_DTMF_3),
            DialerDigit(digit = "4", letters = "GHI",  dtmfTone = ToneGenerator.TONE_DTMF_4),
            DialerDigit(digit = "5", letters = "JKL",  dtmfTone = ToneGenerator.TONE_DTMF_5),
            DialerDigit(digit = "6", letters = "MNO",  dtmfTone = ToneGenerator.TONE_DTMF_6),
            DialerDigit(digit = "7", letters = "PQRS", dtmfTone = ToneGenerator.TONE_DTMF_7),
            DialerDigit(digit = "8", letters = "TUV",  dtmfTone = ToneGenerator.TONE_DTMF_8),
            DialerDigit(digit = "9", letters = "WXYZ", dtmfTone = ToneGenerator.TONE_DTMF_9),
            DialerDigit(digit = "*", letters = "",     dtmfTone = ToneGenerator.TONE_DTMF_S),
            DialerDigit(digit = "0", letters = "+",    dtmfTone = ToneGenerator.TONE_DTMF_0),
            DialerDigit(digit = "#", letters = "",     dtmfTone = ToneGenerator.TONE_DTMF_P)
        )

        /** Returns the [DialerDigit] matching [digit], or null if not found. */
        fun forDigit(digit: String): DialerDigit? = ALL.firstOrNull { it.digit == digit }
    }
}
