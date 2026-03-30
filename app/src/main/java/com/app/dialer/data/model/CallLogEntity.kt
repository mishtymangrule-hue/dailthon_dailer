package com.app.dialer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.dialer.data.local.CallTypeConverter
import com.app.dialer.domain.model.CallType

/**
 * Room entity representing a single call log entry persisted locally.
 *
 * Mirrors the [com.app.dialer.domain.model.RecentCall] domain model.
 * [CallType] is stored as its enum name string via [CallTypeConverter]
 * (e.g. "MISSED") — human-readable and safe across enum reorderings.
 */
@Entity(tableName = "call_logs")
@TypeConverters(CallTypeConverter::class)
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "contact_name")
    val contactName: String?,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    /** Call classification stored as enum name string (e.g. "MISSED"). */
    @ColumnInfo(name = "call_type")
    val callType: CallType,

    /** Total duration of the answered call in seconds. 0 for missed/rejected/blocked. */
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Long,

    /** Unix timestamp in milliseconds when the call started. */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    /**
     * String representation of the contact photo URI, or null if unavailable.
     * Stored as String because Room cannot persist [android.net.Uri] natively.
     */
    @ColumnInfo(name = "photo_uri")
    val photoUri: String?,

    /** False for unread missed calls; true once the user has seen the call. */
    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    /**
     * 0-based index of the SIM slot on which the call was placed or received.
     * -1 when the slot index is unknown.
     */
    @ColumnInfo(name = "sim_slot_index")
    val simSlotIndex: Int = -1
)
