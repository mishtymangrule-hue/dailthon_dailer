package com.app.dialer.domain.usecase

import com.app.dialer.domain.model.CallLogEntry
import com.app.dialer.domain.repository.CallLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case: observe the full call log, ordered by most recent first.
 *
 * Returns a [Flow] so the presentation layer can react to changes
 * (e.g., new calls added by InCallService) without polling.
 */
class ObserveCallLogUseCase @Inject constructor(
    private val repository: CallLogRepository
) {
    operator fun invoke(): Flow<List<CallLogEntry>> =
        repository.observeCallLog()
}
