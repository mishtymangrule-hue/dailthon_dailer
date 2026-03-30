package com.app.dialer.core.utils

import android.content.Context
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility for formatting and analysing phone number strings.
 *
 * All methods are safe to call from any thread. Format operations use only
 * [PhoneNumberUtils] (platform API — no third-party libphonenumber dependency).
 *
 * ### Extension separators
 * `,` (pause) and `;` (wait) are recognised as extension separators. The main
 * number part is formatted independently and the suffix (`,` / `;` + extension
 * digits) is re-appended verbatim after formatting.
 *
 * ### Heuristic formatting fallback
 * When [PhoneNumberUtils.formatNumber] returns null (e.g. short or non-standard
 * numbers), a simple digit-count heuristic inserts spaces for readability.
 */
@Singleton
class PhoneNumberFormatter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val telephonyManager: TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    companion object {
        private const val TAG = "PhoneNumberFormatter"
    }

    /**
     * Formats [rawInput] for display.
     *
     * Processing steps:
     * 1. Detect and split off any extension suffix (, or ;).
     * 2. Strip non-dialable characters from the main part (keep digits and leading +).
    * 3. Delegate to [PhoneNumberUtils.formatNumber] with the current system region.
     * 4. Fall back to [heuristicFormat] when the platform formatter returns null.
     * 5. Re-append the extension suffix verbatim.
     *
     * @param rawInput Raw string as typed by the user.
     * @return Display-ready formatted string, or [rawInput] unchanged on error.
     */
    fun format(rawInput: String): String {
        if (rawInput.isBlank()) return rawInput

        return try {
            val extIndex = rawInput.indexOfFirst { it == ',' || it == ';' }
            val mainPart = if (extIndex != -1) rawInput.substring(0, extIndex) else rawInput
            val extensionSuffix = if (extIndex != -1) rawInput.substring(extIndex) else ""

            val stripped = stripFormatting(mainPart)
            if (stripped.isEmpty()) return rawInput

            val formatted = PhoneNumberUtils.formatNumber(stripped, defaultRegion())
                ?: heuristicFormat(stripped)

            formatted + extensionSuffix
        } catch (e: Exception) {
            Log.w(TAG, "Failed to format number: $rawInput", e)
            rawInput
        }
    }

    /**
     * Strips all formatting characters from [formatted], retaining only digits
     * and a leading `+` (for international prefix).
     *
     * @return Dialable string (digits + optional leading +).
     */
    fun stripFormatting(formatted: String): String = buildString {
        formatted.forEachIndexed { index, ch ->
            when {
                ch == '+' && index == 0 -> append(ch)
                ch.isDigit() -> append(ch)
            }
        }
    }

    /**
     * Returns true when [number] is a recognised emergency number on this device.
     *
     * Uses [TelephonyManager.isEmergencyNumber] on API 29+ and the legacy
     * [PhoneNumberUtils.isEmergencyNumber] on API 26–28.
     *
     * @param number Raw or partially formatted number string to test.
     */
    fun isEmergencyNumber(number: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                telephonyManager.isEmergencyNumber(number)
            } else {
                @Suppress("DEPRECATION")
                PhoneNumberUtils.isEmergencyNumber(number)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check emergency number: $number", e)
            false
        }
    }

    /**
     * Length-based heuristic formatter used when [PhoneNumberUtils] cannot produce output.
     *
     * Groups:
     * - ≤ 4 digits: no spaces
     * - 5–7 digits: AAA BBBB
     * - 8–10 digits: AAA BBB CCCC
     * - 11–12 digits: AA BBBBB CCCCC
     * - 13+ digits: returned unchanged
     */
    private fun heuristicFormat(digits: String): String {
        return when (digits.length) {
            in 1..4  -> digits
            in 5..7  -> "${digits.substring(0, 3)} ${digits.substring(3)}"
            in 8..10 -> "${digits.substring(0, 3)} ${digits.substring(3, 6)} ${digits.substring(6)}"
            in 11..12 -> "${digits.substring(0, 2)} ${digits.substring(2, 7)} ${digits.substring(7)}"
            else     -> digits
        }
    }

    private fun defaultRegion(): String =
        Locale.getDefault().country
            .takeIf { it.length == 2 }
            ?.uppercase(Locale.ROOT)
            ?: "US"
}
