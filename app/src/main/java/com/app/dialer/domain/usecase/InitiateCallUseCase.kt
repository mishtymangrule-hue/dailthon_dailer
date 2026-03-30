package com.app.dialer.domain.usecase

import com.app.dialer.domain.model.SimCard
import com.app.dialer.domain.repository.DialerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Sealed hierarchy of domain-level errors that can occur during call initiation.
 */
sealed class DialerError {
    /** The phone number did not pass validation. */
    object InvalidNumber : DialerError()

    /** The app does not hold the required [android.Manifest.permission.CALL_PHONE] permission. */
    object PermissionDenied : DialerError()

    /** The requested SIM card is unavailable or inactive. */
    object SimUnavailable : DialerError()

    /** Any other unexpected failure. */
    data class Unknown(val message: String) : DialerError()
}

/**
 * Sealed result wrapper emitted by [InitiateCallUseCase] so callers can
 * react to each state transition.
 */
sealed class CallResult {
    /** Call initiation is in progress. */
    object Loading : CallResult()

    /** The call intent was dispatched successfully. */
    object Success : CallResult()

    /**
     * Call initiation failed.
     * @param error Domain-level error describing why.
     */
    data class Failure(val error: DialerError) : CallResult()
}

/**
 * Use case that validates a phone number and then asks the repository to
 * initiate an outgoing call.
 *
 * Emits a [Flow] of [CallResult] so the ViewModel can drive UI state across
 * the loading → success / failure lifecycle.
 *
 * @param repository             Provides the actual call-initiation mechanism.
 * @param validatePhoneNumber    Validates the number before attempting to call.
 */
class InitiateCallUseCase @Inject constructor(
    private val repository: DialerRepository,
    private val validatePhoneNumber: ValidatePhoneNumberUseCase
) {

    /**
     * @param phoneNumber Raw or formatted number to dial.
     * @param simCard     Optional SIM to use. Null → system default.
     */
    operator fun invoke(
        phoneNumber: String,
        simCard: SimCard?
    ): Flow<CallResult> = flow {
        emit(CallResult.Loading)

        if (!validatePhoneNumber(phoneNumber)) {
            emit(CallResult.Failure(DialerError.InvalidNumber))
            return@flow
        }

        if (simCard != null && !simCard.isActive) {
            emit(CallResult.Failure(DialerError.SimUnavailable))
            return@flow
        }

        val subscriptionId = simCard?.subscriptionId

        val result = runCatching {
            repository.initiateCall(phoneNumber, subscriptionId).getOrThrow()
        }

        if (result.isSuccess) {
            emit(CallResult.Success)
        } else {
            val cause = result.exceptionOrNull()
            val dialerError = when {
                cause is SecurityException -> DialerError.PermissionDenied
                cause?.message?.contains("SIM", ignoreCase = true) == true -> DialerError.SimUnavailable
                else -> DialerError.Unknown(cause?.message ?: "Unknown error")
            }
            emit(CallResult.Failure(dialerError))
        }
    }
}
