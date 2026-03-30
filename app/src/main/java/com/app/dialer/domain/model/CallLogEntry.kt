package com.app.dialer.domain.model

/**
 * Domain model representing a single call log entry.
 * Decoupled from both Room entities and Android system call log cursors.
 *
 * Uses [CallType] — the canonical call-classification enum — eliminating the
 * former `CallLogType` bridge enum and its bidirectional system-integer mapper.
 */
data class CallLogEntry(
    val id: Long,
    val phoneNumber: String,
    val contactName: String?,
    val callType: CallType,
    val timestamp: Long,
    val durationSeconds: Long,
    val isRead: Boolean
)
