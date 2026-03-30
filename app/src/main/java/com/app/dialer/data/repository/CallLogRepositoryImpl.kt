package com.app.dialer.data.repository

import com.app.dialer.data.local.CallLogDao
import com.app.dialer.data.model.CallLogEntity
import com.app.dialer.domain.model.CallLogEntry
import com.app.dialer.domain.model.CallType
import com.app.dialer.domain.repository.CallLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [CallLogRepository] backed by the local Room database.
 */
@Singleton
class CallLogRepositoryImpl @Inject constructor(
    private val dao: CallLogDao
) : CallLogRepository {

    override fun observeCallLog(): Flow<List<CallLogEntry>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeRecent(limit: Int): Flow<List<CallLogEntry>> =
        dao.observeRecent(limit).map { entities -> entities.map { it.toDomain() } }

    override fun observeByNumber(phoneNumber: String): Flow<List<CallLogEntry>> =
        dao.observeByNumber(phoneNumber).map { entities -> entities.map { it.toDomain() } }

    override fun observeMissedCallCount(): Flow<Int> =
        dao.observeMissedCallCount()

    override suspend fun insert(entry: CallLogEntry): Long =
        dao.insert(entry.toEntity())

    override suspend fun deleteById(id: Long) =
        dao.deleteById(id)

    override suspend fun markReadByNumber(phoneNumber: String) =
        dao.markReadByNumber(phoneNumber)

    override suspend fun deleteAll() =
        dao.deleteAll()

    // ─── Mapping helpers ──────────────────────────────────────────────────────

    private fun CallLogEntity.toDomain() = CallLogEntry(
        id = id,
        phoneNumber = phoneNumber,
        contactName = contactName,
        callType = CallType.fromSystemValue(callType),
        timestamp = timestamp,
        durationSeconds = durationSeconds,
        isRead = isRead
    )

    private fun CallLogEntry.toEntity() = CallLogEntity(
        id = id,
        phoneNumber = phoneNumber,
        contactName = contactName,
        callType = callType.systemValue,
        timestamp = timestamp,
        durationSeconds = durationSeconds,
        isRead = isRead
    )
}
