package com.app.dialer.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.text.format.DateUtils
import java.util.Locale

// ─── Phone number utilities ───────────────────────────────────────────────────

/**
 * Formats a raw number string into a locale-aware display format.
 * e.g. "5551234567" → "(555) 123-4567"
 */
fun String.formatPhoneNumber(): String =
    PhoneNumberUtils.formatNumber(this, Locale.getDefault().country) ?: this

/**
 * Strips all non-digit characters except '+' from a phone number string,
 * producing a dialable number.
 */
fun String.toDialableNumber(): String =
    filter { it.isDigit() || it == '+' }

/**
 * Returns true if this string could be a valid phone number (7–15 digits).
 */
fun String.isValidPhoneNumber(): Boolean {
    val digits = filter { it.isDigit() }
    return digits.length in 7..15
}

// ─── Duration formatting ──────────────────────────────────────────────────────

/**
 * Converts call duration in seconds to a human-readable format.
 * e.g. 90 → "1:30", 3661 → "1:01:01"
 */
fun Long.formatCallDuration(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}

/**
 * Returns a relative time string (e.g. "3 minutes ago", "Yesterday") for a
 * Unix timestamp in milliseconds.
 */
fun Long.toRelativeTimeString(): String =
    DateUtils.getRelativeTimeSpanString(
        this,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()

// ─── Intent helpers ───────────────────────────────────────────────────────────

/**
 * Creates a dial [Intent] for the given [phoneNumber].
 * Uses the `tel:` URI scheme.
 */
fun createDialIntent(phoneNumber: String): Intent =
    Intent(Intent.ACTION_DIAL).apply {
        data = Uri.fromParts("tel", phoneNumber.toDialableNumber(), null)
    }

/**
 * Creates a direct call [Intent] for the given [phoneNumber].
 * Requires CALL_PHONE permission; caller is responsible for checking it.
 */
fun createCallIntent(phoneNumber: String): Intent =
    Intent(Intent.ACTION_CALL).apply {
        data = Uri.fromParts("tel", phoneNumber.toDialableNumber(), null)
    }

// ─── API level helpers ────────────────────────────────────────────────────────

/** True if the device is running Android 12 (API 31) or higher. */
val isApi31OrAbove: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/** True if the device is running Android 13 (API 33) or higher. */
val isApi33OrAbove: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

/** Executes [block] only on API [minApi] and above, returns null otherwise. */
inline fun <T> runOnApi(minApi: Int, block: () -> T): T? =
    if (Build.VERSION.SDK_INT >= minApi) block() else null

// ─── Context extensions ───────────────────────────────────────────────────────

/**
 * Safely starts an activity, swallowing [android.content.ActivityNotFoundException].
 * Returns true if the activity was started, false otherwise.
 */
fun Context.safeStartActivity(intent: Intent): Boolean {
    return try {
        startActivity(intent)
        true
    } catch (e: android.content.ActivityNotFoundException) {
        false
    }
}
