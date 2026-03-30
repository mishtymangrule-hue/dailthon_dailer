package com.app.dialer.domain.usecase

import com.app.dialer.domain.model.SimCard
import com.app.dialer.domain.repository.DialerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case that returns the list of currently active SIM cards in the device.
 *
 * Wraps [DialerRepository.getAvailableSimCards] as a cold [Flow] so callers can
 * react to the result within a coroutine pipeline without dealing with suspend
 * functions directly.
 *
 * ### Edge cases
 * - **No SIM / Airplane mode**: [DialerRepository.getAvailableSimCards] returns an empty
 *   list; this use case emits that empty list rather than an error, allowing callers to
 *   proceed with a null/default SIM.
 * - **Permission denied**: The repository swallows [SecurityException] and returns an
 *   empty list. The caller (ViewModel) falls back to the system-default SIM.
 * - **Repository failure**: Any [Result.failure] is treated as an empty list so that
 *   the outgoing call can still be attempted with the system-default SIM rather than
 *   failing entirely.
 *
 * @param repository Provides access to device SIM card information.
 */
class GetAvailableSimCardsUseCase @Inject constructor(
    private val repository: DialerRepository
) {
    /**
     * @return A cold Flow that emits a single list of active [SimCard]s and then completes.
     *         The list is empty when no SIM is inserted, in airplane mode, or when
     *         [android.Manifest.permission.READ_PHONE_STATE] is not granted.
     */
    operator fun invoke(): Flow<List<SimCard>> = flow {
        val result = repository.getAvailableSimCards()
        emit(result.getOrDefault(emptyList()))
    }
}
