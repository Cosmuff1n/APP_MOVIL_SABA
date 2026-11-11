package com.example.appsaba.data.repository

import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.repository.ParkingRepository
import com.example.appsaba.domain.repository.ServiceRepository
import com.example.appsaba.data.local.datasource.ParkingLocalDataSource
import com.example.appsaba.data.local.entity.ParkingEntity
import com.example.appsaba.data.mapper.toDomain
import com.example.appsaba.data.mapper.toEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class ParkingRepositoryImpl @Inject constructor(
    private val source: ParkingLocalDataSource
) : ParkingRepository {
    override fun observeAll() = source.observeAll().map { it.map(ParkingEntity::toDomain) }
    override fun observeActive() = source.observeActive().map { it.map(ParkingEntity::toDomain) }
    override suspend fun getById(parkingId: Long) = source.getById(parkingId)?.toDomain()
    override suspend fun insert(parking: Parking) = source.insert(parking.toEntity())
    override suspend fun update(parking: Parking) = source.update(parking.toEntity())
    override suspend fun delete(parking: Parking) = source.delete(parking.toEntity())
}
