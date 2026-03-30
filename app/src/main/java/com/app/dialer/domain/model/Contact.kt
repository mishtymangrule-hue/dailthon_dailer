package com.app.dialer.domain.model

/**
 * Domain model for a device contact.
 * Decoupled from ContentProvider cursors and Room entities.
 */
data class Contact(
    val id: Long,
    val displayName: String,
    val phoneNumbers: List<ContactPhoneNumber>,
    val photoUri: String?,
    val isStarred: Boolean
)

/**
 * Represents a single phone number associated with a [Contact].
 */
data class ContactPhoneNumber(
    val number: String,
    val type: PhoneNumberType,
    val label: String?
)

/**
 * Common phone number type labels (mirrors ContactsContract.CommonDataKinds.Phone).
 */
enum class PhoneNumberType(val systemValue: Int) {
    Mobile(2),
    Home(1),
    Work(3),
    Main(12),
    Other(7),
    Custom(0);

    companion object {
        fun fromSystemValue(value: Int): PhoneNumberType =
            entries.firstOrNull { it.systemValue == value } ?: Other
    }
}
