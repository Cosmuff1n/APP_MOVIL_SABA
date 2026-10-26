package com.example.appsaba.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.appsaba.data.model.RemoteParking
import kotlinx.coroutines.flow.Flow

@Dao
interface RemoteParkingDao {
    @Query("SELECT * FROM remote_parkings ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<RemoteParking>>

    @Query("DELETE FROM remote_parkings")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rows: List<RemoteParking>)

    @Transaction
    suspend fun replaceAll(rows: List<RemoteParking>) {
        clear()
        upsertAll(rows)
    }
}
