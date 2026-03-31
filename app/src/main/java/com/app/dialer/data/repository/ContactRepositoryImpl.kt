package com.app.dialer.data.repository

import com.app.dialer.domain.model.SuggestedContact
import com.app.dialer.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Singleton

/**
 * Legacy Room-backed contact repository (P1).
 *
 * DEPRECATED: Replaced by [DialerContactRepositoryImpl] which queries
 * Android's ContentProvider for real-time dial-pad auto-complete.
 * Kept for reference but not used by the DI module.
 */
@Singleton
class ContactRepositoryImpl : ContactRepository {

    override fun getSuggestedContacts(query: String, limit: Int): Flow<List<SuggestedContact>> =
        emptyFlow()

    override fun searchContacts(query: String): Flow<List<SuggestedContact>> =
        emptyFlow()

    override suspend fun lookupContactByNumber(phoneNumber: String): Result<SuggestedContact?> =
        Result.success(null)
}
