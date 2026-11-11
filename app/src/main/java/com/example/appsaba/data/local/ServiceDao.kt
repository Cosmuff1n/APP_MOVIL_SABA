package com.example.appsaba.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appsaba.data.local.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

/** Servicios tecnologicos: barreras, LPR, POS y cajeros. */
@Dao
interface ServiceDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(service: ServiceEntity): Long

    @Update
    suspend fun update(service: ServiceEntity): Int

    @Delete
    suspend fun delete(service: ServiceEntity): Int

    @Query("SELECT * FROM services WHERE parkingId = :parkingId ORDER BY name COLLATE NOCASE ASC")
    fun observeByParking(parkingId: Long): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :serviceId LIMIT 1")
    suspend fun getById(serviceId: Long): ServiceEntity?

    @Query("SELECT * FROM services WHERE id = :serviceId LIMIT 1")
    fun observeById(serviceId: Long): Flow<ServiceEntity?>

    @Query("UPDATE services SET status = :status, problem = :problem, lastUpdatedAt = :updatedAt WHERE id = :serviceId")
    suspend fun updateStatus(
        serviceId: Long,
        status: String,
        problem: String?,
        updatedAt: Long
    ): Int
}
