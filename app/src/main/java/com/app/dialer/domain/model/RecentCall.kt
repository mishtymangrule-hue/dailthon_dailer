package com.app.dialer.domain.model

import android.net.Uri

/**
 * Domain model representing a single entry in the recent-calls list.
 *
 * @param id             Local database primary key.
 * @param contactName    Display name of the contact, or null if unknown.
 * @param phoneNumber    Raw dialable phone number string.
 * @param callType       Classification of the call (incoming, missed, etc.).
 * @param durationSeconds Total duration of the call in seconds. 0 for missed/rejected/blocked.
 * @param timestamp      Unix timestamp in milliseconds when the call started.
 * @param photoUri       Optional URI for the contact's thumbnail photo.
 * @param isRead         False for unread missed calls; true otherwise.
 * @param simSlotIndex   0-based SIM slot index. -1 when the slot is unknown.
 */
data class RecentCall(
    val id: Long,
    val contactName: String?,
    val phoneNumber: String,
    val callType: RecentCallType,
    val durationSeconds: Long,
    val timestamp: Long,
    val photoUri: Uri?,
    val isRead: Boolean,
    val simSlotIndex: Int
)

/**
 * Classification of a call log entry.
 *
 * Values are aligned with [android.provider.CallLog.Calls] type constants where
 * a direct mapping exists.
 */
enum class RecentCallType {
    INCOMING,
    OUTGOING,
    MISSED,
    REJECTED,
    VOICEMAIL,
    BLOCKED;

    /** Returns the [android.provider.CallLog.Calls] integer for this type. */
    fun toSystemValue(): Int = when (this) {
        INCOMING  -> 1
        OUTGOING  -> 2
        MISSED    -> 3
        REJECTED  -> 5
        VOICEMAIL -> 4
        BLOCKED   -> 6
    }

    companion object {
        /**
         * Converts from [android.provider.CallLog.Calls] type integer to [RecentCallType].
         * Defaults to [INCOMING] for unrecognised values so that unknown entries are still surfaced.
         */
        fun fromSystemValue(value: Int): RecentCallType = when (value) {
            1 -> INCOMING
            2 -> OUTGOING
            3 -> MISSED
            5 -> REJECTED
            4 -> VOICEMAIL
            6 -> BLOCKED
            else -> INCOMING
        }
    }
}
