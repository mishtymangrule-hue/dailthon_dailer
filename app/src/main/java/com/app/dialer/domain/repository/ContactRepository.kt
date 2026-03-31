package com.app.dialer.domain.repository

import com.app.dialer.domain.model.SuggestedContact
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for contact data used by the dialer module.
 *
 * This interface exposes a lighter surface focused on real-time dial-pad
 * auto-complete and reverse lookup. Implementations query Android's
 * [android.provider.ContactsContract] ContentProvider and the local Room cache.
 */
interface ContactRepository {

    /**
     * Returns a live stream of contacts that match [query] against display name or
     * phone number, limited to [limit] results.
     *
     * When [query] is blank, returns starred and frequently-called contacts instead
     * (dial-pad idle state). Emits a new list whenever the underlying data changes.
     *
     * @param query Partial name or number typed into the dial pad. May be empty.
     * @param limit Maximum number of results to return.
     */
    fun getSuggestedContacts(query: String, limit: Int): Flow<List<SuggestedContact>>

    /**
     * Returns a live stream of all contacts whose display name or phone number
     * contains [query].
     *
     * This method performs no limit and is intended for the full search results screen.
     * Debouncing must be applied by the caller (ViewModel).
     *
     * @param query Non-empty search string.
     */
    fun searchContacts(query: String): Flow<List<SuggestedContact>>

    /**
     * Performs a single-shot reverse lookup for [phoneNumber].
     *
     * @return [Result.success] containing the matching [SuggestedContact], or null when
     *         no matching contact is found. [Result.failure] on ContentProvider error.
     */
    suspend fun lookupContactByNumber(phoneNumber: String): Result<SuggestedContact?>
}
