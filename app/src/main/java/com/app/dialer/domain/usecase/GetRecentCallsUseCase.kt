package com.app.dialer.domain.usecase

import com.app.dialer.domain.model.RecentCall
import com.app.dialer.domain.repository.DialerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case that exposes a live stream of the user's most recent calls.
 *
 * Delegates directly to [DialerRepository.getRecentCalls]. The ViewModel
 * collects the resulting [Flow] and converts it to UI state.
 *
 * @param repository Provides access to the call log.
 */
class GetRecentCallsUseCase @Inject constructor(
    private val repository: DialerRepository
) {
    /**
     * @param limit Maximum number of recent call entries to return. Defaults to 10.
     * @return Flow emitting an updated list whenever the call log changes.
     */
    operator fun invoke(limit: Int = 10): Flow<List<RecentCall>> =
        repository.getRecentCalls(limit)
}
