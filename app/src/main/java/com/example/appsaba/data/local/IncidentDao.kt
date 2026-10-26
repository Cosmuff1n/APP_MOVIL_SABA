package com.example.appsaba.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appsaba.data.model.Incident
import kotlinx.coroutines.flow.Flow

/** Historial, consultas y CRUD de tickets/incidencias. */
@Dao
interface IncidentDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(incident: Incident): Long

    @Update
    suspend fun update(incident: Incident): Int

    @Delete
    suspend fun delete(incident: Incident): Int

    @Query("SELECT * FROM incidents WHERE id = :incidentId LIMIT 1")
    suspend fun getById(incidentId: Long): Incident?

    @Query("SELECT * FROM incidents WHERE parkingId = :parkingId ORDER BY reportedAt DESC, id DESC")
    fun observeByParking(parkingId: Long): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE parkingId = :parkingId AND isResolved = 0 ORDER BY reportedAt DESC, id DESC")
    fun observeActiveByParking(parkingId: Long): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE serviceId = :serviceId ORDER BY reportedAt DESC, id DESC")
    fun observeByService(serviceId: Long): Flow<List<Incident>>

    @Query("SELECT COUNT(*) FROM incidents WHERE parkingId = :parkingId AND isResolved = 0")
    fun observeActiveCount(parkingId: Long): Flow<Int>
}
