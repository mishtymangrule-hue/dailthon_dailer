package com.app.dialer.domain.usecase

import com.app.dialer.domain.model.SuggestedContact
import com.app.dialer.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case that returns a live stream of contact suggestions for the dial pad.
 *
 * ### Behaviour
 * - **Empty query** → returns starred and frequently-called contacts, limited to 5.
 *   This is the idle state shown before the user starts typing.
 * - **Non-empty query** → delegates to [ContactRepository.searchContacts], which
 *   performs a full-text match against names and numbers with no hard cap.
 *
 * Debouncing (to avoid querying on every keystroke) must be applied by the
 * ViewModel using `debounce`, not in this use case, to keep the use case testable
 * and side-effect-free.
 *
 * @param contactRepository Provides contact data from the system ContentProvider / cache.
 */
class GetSuggestedContactsUseCase @Inject constructor(
    private val contactRepository: ContactRepository
) {
    /** Idle-state suggestion limit (starred + frequent). */
    private val idleSuggestionLimit = 5

    /**
     * @param query String currently entered in the dial pad. May be empty.
     * @return Flow emitting an updated list whenever the underlying data changes.
     */
    operator fun invoke(query: String): Flow<List<SuggestedContact>> {
        return if (query.isBlank()) {
            contactRepository.getSuggestedContacts(query = "", limit = idleSuggestionLimit)
        } else {
            contactRepository.searchContacts(query)
        }
    }
}
