package com.app.dialer.domain.repository

import com.app.dialer.domain.model.CallLogEntry
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for call log data.
 * Implementations in the data layer read from Android's system CallLog
 * ContentProvider and/or the local Room database.
 */
interface CallLogRepository {

    /** Observe all call log entries, ordered by timestamp descending. */
    fun observeCallLog(): Flow<List<CallLogEntry>>

    /** Observe the most recent [limit] call log entries. */
    fun observeRecent(limit: Int): Flow<List<CallLogEntry>>

    /** Observe call log entries for a specific [phoneNumber]. */
    fun observeByNumber(phoneNumber: String): Flow<List<CallLogEntry>>

    /** Observe the count of unread missed calls. */
    fun observeMissedCallCount(): Flow<Int>

    /** Insert a new call log entry and return its assigned ID. */
    suspend fun insert(entry: CallLogEntry): Long

    /** Delete a call log entry by its [id]. */
    suspend fun deleteById(id: Long)

    /** Mark all entries for [phoneNumber] as read. */
    suspend fun markReadByNumber(phoneNumber: String)

    /** Delete all call log entries. */
    suspend fun deleteAll()
}
