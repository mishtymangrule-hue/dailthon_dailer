package com.app.dialer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for locally cached contact data.
 *
 * Caches frequently accessed fields from the system ContactsProvider to
 * reduce repeated ContentResolver queries across features.
 * Prompt 2 should expand this into multi-number support, either by storing one
 * row per contact-number pair or by normalizing phone numbers into a child table.
 */
@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey
    @ColumnInfo(name = "contact_id")
    val contactId: Long,

    @ColumnInfo(name = "display_name")
    val displayName: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "phone_type")
    val phoneType: Int = 0,

    @ColumnInfo(name = "photo_uri")
    val photoUri: String?,

    @ColumnInfo(name = "is_starred")
    val isStarred: Boolean = false,

    /** Timestamp of the last cache sync in milliseconds. */
    @ColumnInfo(name = "last_synced")
    val lastSynced: Long = 0L
)
