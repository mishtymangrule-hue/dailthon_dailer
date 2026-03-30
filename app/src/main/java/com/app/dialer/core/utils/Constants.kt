package com.app.dialer.core.utils

/**
 * Application-wide constants.
 */
object Constants {

    // ─── Database ─────────────────────────────────────────────────────────────
    const val DATABASE_NAME = "dialer_db"
    const val DATABASE_VERSION = 1

    // ─── DataStore ────────────────────────────────────────────────────────────
    const val PREFERENCES_NAME = "dialer_preferences"

    // ─── Notification channels ────────────────────────────────────────────────
    const val NOTIFICATION_CHANNEL_CALL = "dialer_call_channel"
    const val NOTIFICATION_CHANNEL_MISSED = "dialer_missed_call_channel"

    // ─── Notification IDs ─────────────────────────────────────────────────────
    const val NOTIFICATION_ID_IN_CALL = 1001
    const val NOTIFICATION_ID_MISSED_CALL = 1002

    // ─── Intent extras ────────────────────────────────────────────────────────
    const val EXTRA_CALL_ID = "extra_call_id"
    const val EXTRA_PHONE_NUMBER = "extra_phone_number"

    // ─── Call log limits ──────────────────────────────────────────────────────
    const val MAX_CALL_LOG_ENTRIES = 500
    const val RECENT_CALLS_LIMIT = 50

    // ─── Default values ───────────────────────────────────────────────────────
    const val DEFAULT_RINGTONE_DURATION_MS = 30_000L
    const val DTMF_TONE_DURATION_MS = 120L
    const val KEYPAD_LONG_PRESS_TIMEOUT_MS = 400L

    // ─── Contact avatar ───────────────────────────────────────────────────────
    const val CONTACT_AVATAR_SIZE_DP = 48
    const val CONTACT_AVATAR_LARGE_SIZE_DP = 96
}
