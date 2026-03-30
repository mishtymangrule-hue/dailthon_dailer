package com.app.dialer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.dialer.data.model.RecentCallEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the `recent_calls` table managed by the dialer module.
 *
 * All write operations are suspend functions; read operations that must stay
 * reactive return [Flow] so the UI layer can observe changes without polling.
 */
@Dao
interface RecentCallDao {

    /**
     * Observes the [limit] most recent call entries ordered by timestamp descending.
     * Emits a new list whenever any row in `recent_calls` changes.
     */
    @Query("SELECT * FROM recent_calls ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentCalls(limit: Int): Flow<List<RecentCallEntity>>

    /**
     * Inserts a new call log entry. If a row with the same primary key already
     * exists it is replaced entirely.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(entity: RecentCallEntity)

    /**
     * Updates an existing call log entry identified by its [RecentCallEntity.id].
     * No-op if the entry does not exist.
     */
    @Update
    suspend fun updateCallLog(entity: RecentCallEntity)

    /**
     * Permanently deletes the call log entry with the given [id].
     * No-op if no matching row exists.
     */
    @Query("DELETE FROM recent_calls WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Marks the call log entry identified by [id] as read by setting [is_read] = 1.
     * No-op if no matching row exists.
     */
    @Query("UPDATE recent_calls SET is_read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    /**
     * Observes the count of unread missed calls (call_type = 'MISSED' AND is_read = 0).
     * Emits a new count whenever any relevant row changes.
     */
    @Query("SELECT COUNT(*) FROM recent_calls WHERE call_type = 'MISSED' AND is_read = 0")
    fun getUnreadMissedCallCount(): Flow<Int>
}
