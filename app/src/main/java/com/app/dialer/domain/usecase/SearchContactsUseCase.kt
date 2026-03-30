package com.app.dialer.domain.usecase

import com.app.dialer.domain.model.Contact
import com.app.dialer.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case: search contacts whose display name or phone number contains the given query.
 *
 * When [query] is blank the full contacts list is returned (equivalent to
 * browsing all contacts). Callers that need a "no results on empty input"
 * behaviour should check [query].isBlank() before invoking.
 */
class SearchContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    operator fun invoke(query: String): Flow<List<Contact>> =
        repository.searchContacts(query)
}
