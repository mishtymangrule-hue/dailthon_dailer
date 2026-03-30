package com.app.dialer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a single call log entry persisted locally.
 *
 * This supplements (and may cache) Android's system call log ContentProvider.
 */
@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "contact_name")
    val contactName: String?,

    /** Call type: 1 = incoming, 2 = outgoing, 3 = missed */
    @ColumnInfo(name = "call_type")
    val callType: Int,

    /** Unix timestamp in milliseconds when the call occurred. */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    /** Duration of the call in seconds. 0 for missed/rejected. */
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Long,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false
)
