package com.app.dialer.data.repository

import com.app.dialer.domain.model.SuggestedContact
import com.app.dialer.domain.repository.DialerContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub implementation of [DialerContactRepository].
 *
 * Returns empty results for all queries. The real implementation — which queries
 * [android.provider.ContactsContract] via a ContentResolver and caches results in
 * the Room `contacts` table — will be provided in Prompt 2C (Contacts module).
 *
 * This stub exists solely so that Hilt can satisfy the [DialerContactRepository]
 * binding at compile time and the app runs end-to-end without the Contacts module.
 */
@Singleton
class DialerContactRepositoryImpl @Inject constructor() : DialerContactRepository {

    override fun getSuggestedContacts(query: String, limit: Int): Flow<List<SuggestedContact>> =
        flowOf(emptyList())

    override fun searchContacts(query: String): Flow<List<SuggestedContact>> =
        flowOf(emptyList())

    override suspend fun lookupContactByNumber(phoneNumber: String): Result<SuggestedContact?> =
        Result.success(null)
}
