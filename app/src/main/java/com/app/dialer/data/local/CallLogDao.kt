package com.app.dialer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.dialer.data.model.CallLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallLogDao {

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<CallLogEntity>>

    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentCalls(limit: Int): Flow<List<CallLogEntity>>

    @Query("SELECT * FROM call_logs WHERE phone_number = :number ORDER BY timestamp DESC")
    fun observeByNumber(number: String): Flow<List<CallLogEntity>>

    @Query("SELECT * FROM call_logs WHERE id = :id")
    suspend fun getById(id: Long): CallLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(entry: CallLogEntity): Long

    @Update
    suspend fun updateCallLog(entry: CallLogEntity)

    @Query("DELETE FROM call_logs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM call_logs")
    suspend fun deleteAll()

    @Query("UPDATE call_logs SET is_read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE call_logs SET is_read = 1 WHERE phone_number = :number")
    suspend fun markReadByNumber(number: String)

    @Query("SELECT COUNT(*) FROM call_logs WHERE is_read = 0 AND call_type = 'MISSED'")
    fun getUnreadMissedCallCount(): Flow<Int>
}
