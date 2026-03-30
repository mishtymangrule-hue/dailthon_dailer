package com.app.dialer.domain.model

/**
 * Domain model representing a single call log entry.
 * Decoupled from both Room entities and Android system call log cursors.
 */
data class CallLogEntry(
    val id: Long,
    val phoneNumber: String,
    val contactName: String?,
    val callType: CallLogType,
    val timestamp: Long,
    val durationSeconds: Long,
    val isRead: Boolean
)

/**
 * Call type classification matching Android's [android.provider.CallLog.Calls] constants.
 * Used exclusively by the legacy [CallLogEntry] / [CallLogRepository] pipeline.
 */
enum class CallLogType(val systemValue: Int) {
    Incoming(1),
    Outgoing(2),
    Missed(3),
    Rejected(5),
    Blocked(6),
    Unknown(0);

    companion object {
        fun fromSystemValue(value: Int): CallLogType =
            entries.firstOrNull { it.systemValue == value } ?: Unknown
    }
}
