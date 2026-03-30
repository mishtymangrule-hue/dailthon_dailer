package com.app.dialer.domain.model

import android.net.Uri

/**
 * A contact surface suggested to the user while they type in the dial pad.
 *
 * @param id           System contact ID from [android.provider.ContactsContract.Contacts._ID].
 * @param displayName  Full display name of the contact.
 * @param phoneNumber  The primary phone number string to dial.
 * @param photoUri     Optional URI for the contact's thumbnail photo.
 * @param isStarred    True when the contact is in the user's favourites.
 * @param callCount    Number of times this number has been called; used for ranking suggestions.
 */
data class SuggestedContact(
    val id: Long,
    val displayName: String,
    val phoneNumber: String,
    val photoUri: Uri?,
    val isStarred: Boolean,
    val callCount: Int
)
