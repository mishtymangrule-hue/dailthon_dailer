package com.app.dialer.data.repository

import com.app.dialer.data.local.CallLogDao
import com.app.dialer.data.model.CallLogEntity
import com.app.dialer.domain.model.CallLogType
import com.app.dialer.domain.model.CallLogEntry
import com.app.dialer.domain.model.CallType
import com.app.dialer.domain.repository.CallLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [CallLogRepository] backed by the local Room database.
 *
 * [CallLogEntity] stores [CallType] (the spec-canonical enum); [CallLogEntry] uses
 * the legacy [CallLogType] enum. The mapper bridges the two via their shared
 * Android system integer values.
 */
@Singleton
class CallLogRepositoryImpl @Inject constructor(
    private val dao: CallLogDao
) : CallLogRepository {

    override fun observeCallLog(): Flow<List<CallLogEntry>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeRecent(limit: Int): Flow<List<CallLogEntry>> =
        dao.getRecentCalls(limit).map { entities -> entities.map { it.toDomain() } }

    override fun observeByNumber(phoneNumber: String): Flow<List<CallLogEntry>> =
        dao.observeByNumber(phoneNumber).map { entities -> entities.map { it.toDomain() } }

    override fun observeMissedCallCount(): Flow<Int> =
        dao.getUnreadMissedCallCount()

    override suspend fun insert(entry: CallLogEntry): Long =
        dao.insertCallLog(entry.toEntity())

    override suspend fun deleteById(id: Long) =
        dao.deleteById(id)

    override suspend fun markReadByNumber(phoneNumber: String) =
        dao.markReadByNumber(phoneNumber)

    override suspend fun deleteAll() =
        dao.deleteAll()

    // ─── Mapping helpers ──────────────────────────────────────────────────────

    /**
     * Entity → domain: converts [CallType] (canonical enum) to [CallLogType] (legacy enum)
     * via the shared Android system integer value.
     */
    private fun CallLogEntity.toDomain() = CallLogEntry(
        id = id,
        phoneNumber = phoneNumber,
        contactName = contactName,
        callType = CallLogType.fromSystemValue(callType.toSystemValue()),
        timestamp = timestamp,
        durationSeconds = durationSeconds,
        isRead = isRead
    )

    /**
     * Domain → entity: converts [CallLogType] (legacy enum) to [CallType] (canonical enum)
     * via the shared Android system integer value. photoUri and simSlotIndex are absent
     * from the legacy [CallLogEntry] model and default to null / -1.
     */
    private fun CallLogEntry.toEntity() = CallLogEntity(
        id = id,
        contactName = contactName,
        phoneNumber = phoneNumber,
        callType = CallType.fromSystemValue(callType.systemValue),
        durationSeconds = durationSeconds,
        timestamp = timestamp,
        photoUri = null,
        isRead = isRead,
        simSlotIndex = -1
    )
}
