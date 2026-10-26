package com.example.appsaba.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appsaba.data.model.Parking
import kotlinx.coroutines.flow.Flow

/** Catalogo local de estacionamientos y sus plazas. */
@Dao
interface ParkingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(parking: Parking): Long

    @Update
    suspend fun update(parking: Parking): Int

    @Delete
    suspend fun delete(parking: Parking): Int

    @Query("SELECT * FROM parkings ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<Parking>>

    @Query("SELECT * FROM parkings WHERE isActive = 1 ORDER BY name COLLATE NOCASE ASC")
    fun observeActive(): Flow<List<Parking>>

    @Query("SELECT * FROM parkings WHERE id = :parkingId LIMIT 1")
    suspend fun getById(parkingId: Long): Parking?
}
