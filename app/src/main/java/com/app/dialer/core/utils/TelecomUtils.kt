package com.app.dialer.core.utils

import android.content.Context
import android.net.Uri
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log

/**
 * Stateless utility functions for Telecom-related operations.
 *
 * All functions are pure `fun` members of a singleton `object`; no DI is needed.
 * Callers should ensure required permissions are held before invoking any method
 * that reads sensitive data (call history, default SIM, etc.).
 */
object TelecomUtils {

    private const val TAG = "TelecomUtils"

    // ─── Phone number helpers ────────────────────────────────────────────────

    /**
     * Normalises [rawNumber] for dialling by stripping formatting characters
     * (spaces, dashes, parentheses, dots) while preserving leading `+` for
     * international numbers and `*` / `#` for service codes.
     */
    fun normaliseNumber(rawNumber: String): String =
        rawNumber.replace(Regex("[()\\- .]"), "")

    /**
     * Returns `true` if [number] is a valid dialable string (non-blank and
     * contains at least one digit).
     */
    fun isDialable(number: String): Boolean =
        number.isNotBlank() && number.any { it.isDigit() }

    /**
     * Converts a raw key-press character (0–9, *, #) to the corresponding
     * [android.media.ToneGenerator] DTMF tone constant.
     *
     * Returns `null` for characters that have no DTMF mapping.
     */
    fun charToDtmfTone(char: Char): Int? = when (char) {
        '0' -> android.media.ToneGenerator.TONE_DTMF_0
        '1' -> android.media.ToneGenerator.TONE_DTMF_1
        '2' -> android.media.ToneGenerator.TONE_DTMF_2
        '3' -> android.media.ToneGenerator.TONE_DTMF_3
        '4' -> android.media.ToneGenerator.TONE_DTMF_4
        '5' -> android.media.ToneGenerator.TONE_DTMF_5
        '6' -> android.media.ToneGenerator.TONE_DTMF_6
        '7' -> android.media.ToneGenerator.TONE_DTMF_7
        '8' -> android.media.ToneGenerator.TONE_DTMF_8
        '9' -> android.media.ToneGenerator.TONE_DTMF_9
        '*' -> android.media.ToneGenerator.TONE_DTMF_S
        '#' -> android.media.ToneGenerator.TONE_DTMF_P
        else -> null
    }

    // ─── URI helpers ─────────────────────────────────────────────────────────

    /**
     * Builds a `tel:` [Uri] from [number], normalising it first.
     */
    fun buildTelUri(number: String): Uri =
        Uri.parse("tel:${normaliseNumber(number)}")

    /**
     * Extracts the phone number string from a `tel:` [Uri].
     * Returns null if the URI scheme is not `tel` or the number is blank.
     */
    fun extractNumber(uri: Uri?): String? {
        if (uri?.scheme?.lowercase() != "tel") return null
        return uri.schemeSpecificPart.takeIf { it.isNotBlank() }
    }

    // ─── PhoneAccount helpers ────────────────────────────────────────────────

    /**
     * Returns the default outgoing [PhoneAccountHandle] from [TelecomManager],
     * or `null` if none is set (e.g. no SIM card, or multiple SIMs with no
     * default configured).
     *
     * Requires [android.Manifest.permission.READ_PHONE_STATE].
     */
    fun getDefaultOutgoingAccount(context: Context): PhoneAccountHandle? {
        return try {
            val tm = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            @Suppress("MissingPermission")
            tm.userSelectedOutgoingPhoneAccount
        } catch (e: SecurityException) {
            Log.w(TAG, "getDefaultOutgoingAccount: missing READ_PHONE_STATE", e)
            null
        }
    }

    // ─── Display helpers ─────────────────────────────────────────────────────

    /**
     * Formats a call duration expressed in seconds as `mm:ss` or `h:mm:ss`.
     */
    fun formatDuration(totalSeconds: Long): String {
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s)
        else "%02d:%02d".format(m, s)
    }

    /**
     * Returns a display-friendly label for [number].
     *
     * Strips the `+` prefix for numbers shorter than 7 digits (likely short codes),
     * and adds spaces for international numbers for readability.
     */
    fun formatForDisplay(number: String): String {
        val normalised = normaliseNumber(number)
        return when {
            normalised.startsWith("+") && normalised.length >= 7 -> normalised
            normalised.length <= 6 -> normalised          // short code / speed-dial
            else -> normalised
        }
    }
}
