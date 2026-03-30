package com.app.dialer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.dialer.data.model.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY display_name ASC")
    fun observeAll(): Flow<List<ContactEntity>>

    @Query(
        """SELECT * FROM contacts
           WHERE display_name LIKE '%' || :query || '%'
              OR phone_number LIKE '%' || :query || '%'
           ORDER BY display_name ASC"""
    )
    fun observeSearch(query: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE is_starred = 1 ORDER BY display_name ASC")
    fun observeFavorites(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE contact_id = :id")
    suspend fun getById(id: Long): ContactEntity?

    @Query("SELECT * FROM contacts WHERE phone_number = :number LIMIT 1")
    suspend fun getByPhoneNumber(number: String): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<ContactEntity>)

    @Update
    suspend fun update(contact: ContactEntity)

    @Delete
    suspend fun delete(contact: ContactEntity)

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()
}
