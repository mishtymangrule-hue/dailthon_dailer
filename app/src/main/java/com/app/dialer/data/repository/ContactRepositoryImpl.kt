package com.app.dialer.data.repository

import com.app.dialer.data.local.ContactDao
import com.app.dialer.data.model.ContactEntity
import com.app.dialer.domain.model.Contact
import com.app.dialer.domain.model.ContactPhoneNumber
import com.app.dialer.domain.model.PhoneNumberType
import com.app.dialer.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [ContactRepository] backed by the local Room cache.
 *
 * Full sync with the system ContactsProvider will be implemented in a subsequent
 * prompt. For now, [syncContacts] is a no-op and data flows from Room only.
 */
@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val dao: ContactDao
) : ContactRepository {

    override fun observeAll(): Flow<List<Contact>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun searchContacts(query: String): Flow<List<Contact>> =
        dao.observeSearch(query).map { entities -> entities.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<Contact>> =
        dao.observeFavorites().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: Long): Contact? =
        dao.getById(id)?.toDomain()

    override suspend fun getByPhoneNumber(phoneNumber: String): Contact? =
        dao.getByPhoneNumber(phoneNumber)?.toDomain()

    override suspend fun syncContacts() {
        // Full ContentProvider sync implemented in Prompt 2 (Contacts module).
    }

    // ─── Mapping helpers ──────────────────────────────────────────────────────
    /**
     * Maps a [ContactEntity] to a [Contact] domain model.
     *
     * **P1 limitation — lossy single-number mapping:** [ContactEntity] stores only
     * one phone number per row (`phoneNumber` column). This mapping therefore always
     * produces a single-element [phoneNumbers] list, which is lossy for contacts that
     * have multiple phone numbers in the system ContactsProvider.
     *
     * Prompt 2 (Contacts module) will fix this by querying all numbers for the contact
     * from [android.provider.ContactsContract.CommonDataKinds.Phone] using the
     * contact ID and mapping them into a full multi-element list.
     */    private fun ContactEntity.toDomain() = Contact(
        id = contactId,
        displayName = displayName,
        phoneNumbers = listOf(
            ContactPhoneNumber(
                number = phoneNumber,
                type = PhoneNumberType.fromSystemValue(phoneType),
                label = null
            )
        ),
        photoUri = photoUri,
        isStarred = isStarred
    )
}
