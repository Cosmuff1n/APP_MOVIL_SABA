package com.example.appsaba.data.local.datasource

import com.example.appsaba.data.local.entity.ParkingEntity
import com.example.appsaba.data.local.ParkingDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface ParkingLocalDataSource {
    fun observeAll(): Flow<List<ParkingEntity>>
    fun observeActive(): Flow<List<ParkingEntity>>
    suspend fun getById(parkingId: Long): ParkingEntity?
    suspend fun insert(parking: ParkingEntity): Long
    suspend fun update(parking: ParkingEntity): Int
    suspend fun delete(parking: ParkingEntity): Int
}

class ParkingLocalDataSourceImpl @Inject constructor(
    private val dao: ParkingDao
) : ParkingLocalDataSource {
    override fun observeAll() = dao.observeAll()
    override fun observeActive() = dao.observeActive()
    override suspend fun getById(parkingId: Long) = dao.getById(parkingId)
    override suspend fun insert(parking: ParkingEntity) = dao.insert(parking)
    override suspend fun update(parking: ParkingEntity) = dao.update(parking)
    override suspend fun delete(parking: ParkingEntity) = dao.delete(parking)
}
