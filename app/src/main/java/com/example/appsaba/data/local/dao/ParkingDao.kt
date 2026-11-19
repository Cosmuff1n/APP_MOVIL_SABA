package com.example.appsaba.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.appsaba.data.local.entity.ParkingEntity
import kotlinx.coroutines.flow.Flow

/** Catalogo local de estacionamientos y sus plazas. */
@Dao
interface ParkingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(parking: ParkingEntity): Long

    @Update
    suspend fun update(parking: ParkingEntity): Int

    @Delete
    suspend fun delete(parking: ParkingEntity): Int

    @Query("SELECT * FROM parkings WHERE remoteId IS NULL ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<ParkingEntity>>

    @Query("SELECT * FROM parkings WHERE remoteId IS NULL AND isActive = 1 ORDER BY name COLLATE NOCASE ASC")
    fun observeActive(): Flow<List<ParkingEntity>>

    /** EC2 uses the same parkings table, with a disjoint key range and metadata. */
    @Query("SELECT * FROM parkings WHERE remoteId IS NOT NULL ORDER BY name COLLATE NOCASE ASC")
    fun observeRemote(): Flow<List<ParkingEntity>>

    @Upsert
    suspend fun upsertRemote(parking: ParkingEntity)

    @Query("DELETE FROM parkings WHERE remoteId = :remoteId")
    suspend fun deleteCachedRemote(remoteId: Long)

    @Query("SELECT * FROM parkings WHERE id = :parkingId LIMIT 1")
    suspend fun getById(parkingId: Long): ParkingEntity?
}
