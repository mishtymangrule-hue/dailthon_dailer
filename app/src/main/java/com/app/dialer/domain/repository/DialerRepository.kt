package com.app.dialer.domain.repository

import com.app.dialer.domain.model.RecentCall
import com.app.dialer.domain.model.SimCard
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for core dialer operations: call initiation, call log,
 * and SIM card management.
 *
 * Implementations in the data layer interact with [android.telecom.TelecomManager],
 * the system call-log [android.provider.CallLog.Calls] ContentProvider, and the
 * local Room cache.
 */
interface DialerRepository {

    /**
     * Initiates an outgoing call to [phoneNumber].
     *
     * @param phoneNumber    E.164 or dialable phone number string.
     * @param subscriptionId The [android.telephony.SubscriptionManager] subscription ID
     *                       identifying which SIM to use, or null to use the system default.
     * @return [Result.success] when the call intent was dispatched without error;
     *         [Result.failure] with a descriptive [Exception] otherwise.
     */
    suspend fun initiateCall(phoneNumber: String, subscriptionId: Int?): Result<Unit>

    /**
     * Observes the most recent [limit] call log entries, ordered by timestamp descending.
     * Emits a new list whenever the underlying data changes.
     */
    fun getRecentCalls(limit: Int): Flow<List<RecentCall>>

    /**
     * Marks the call log entry identified by [callId] as read.
     *
     * @return [Result.success] on success; [Result.failure] if the entry was not found
     *         or the operation failed.
     */
    suspend fun markCallAsRead(callId: Long): Result<Unit>

    /**
     * Permanently deletes the call log entry identified by [callId].
     *
     * @return [Result.success] on success; [Result.failure] if the entry was not found
     *         or the deletion failed.
     */
    suspend fun deleteCallLog(callId: Long): Result<Unit>

    /**
     * Returns all available and active SIM cards in the device.
     *
     * Requires [android.Manifest.permission.READ_PHONE_STATE].
     *
     * @return [Result.success] with the list (may be empty on single-SIM or no-SIM devices);
     *         [Result.failure] if the permission is absent or the query fails.
     */
    suspend fun getAvailableSimCards(): Result<List<SimCard>>

    /**
     * Observes the currently configured default SIM card for outgoing calls.
     * Emits null when no default is set or no SIM is available.
     */
    fun getDefaultSimCard(): Flow<SimCard?>
}
