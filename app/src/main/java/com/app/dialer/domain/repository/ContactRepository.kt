package com.app.dialer.domain.repository

import com.app.dialer.domain.model.Contact
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for contact data.
 * Implementations query both Android's ContactsProvider and the local Room cache.
 */
interface ContactRepository {

    /** Observe all contacts, sorted alphabetically by display name. */
    fun observeAll(): Flow<List<Contact>>

    /** Observe contacts whose name or number contains [query]. */
    fun searchContacts(query: String): Flow<List<Contact>>

    /** Observe starred/favourite contacts. */
    fun observeFavorites(): Flow<List<Contact>>

    /** Look up a contact by its system contact ID. Returns null if not found. */
    suspend fun getById(id: Long): Contact?

    /** Look up a contact by phone number. Returns null if not found. */
    suspend fun getByPhoneNumber(phoneNumber: String): Contact?

    /** Sync the local Room cache from the system ContactsProvider. */
    suspend fun syncContacts()
}
