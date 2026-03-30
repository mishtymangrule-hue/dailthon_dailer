package com.app.dialer.data.repository

import com.app.dialer.data.datasource.ContactDataSource
import com.app.dialer.domain.model.SuggestedContact
import com.app.dialer.domain.repository.DialerContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real [DialerContactRepository] backed by Android's [ContactsContract] ContentProvider
 * via [ContactDataSource].
 *
 * ### Threading
 * All ContentResolver reads run on [Dispatchers.IO]. The [Flow]-returning methods
 * use `flowOn(Dispatchers.IO)`; the suspending [lookupContactByNumber] uses
 * `withContext(Dispatchers.IO)`.
 *
 * ### Permissions
 * Callers are responsible for ensuring [android.Manifest.permission.READ_CONTACTS]
 * is held before subscribing to any flow. The [ContactDataSource] will return
 * empty lists on [SecurityException] rather than crashing.
 */
@Singleton
class DialerContactRepositoryImpl @Inject constructor(
    private val contactDataSource: ContactDataSource
) : DialerContactRepository {

    override fun getSuggestedContacts(query: String, limit: Int): Flow<List<SuggestedContact>> =
        flow {
            emit(contactDataSource.getSuggestedContacts(query, limit))
        }.flowOn(Dispatchers.IO)

    override fun searchContacts(query: String): Flow<List<SuggestedContact>> =
        flow {
            emit(contactDataSource.searchContacts(query))
        }.flowOn(Dispatchers.IO)

    override suspend fun lookupContactByNumber(phoneNumber: String): Result<SuggestedContact?> =
        withContext(Dispatchers.IO) {
            runCatching { contactDataSource.lookupByNumber(phoneNumber) }
        }
}
