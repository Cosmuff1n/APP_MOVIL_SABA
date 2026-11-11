package com.example.appsaba.data.repository

import com.example.appsaba.domain.model.Service
import com.example.appsaba.domain.repository.ServiceRepository
import com.example.appsaba.data.local.datasource.ServiceLocalDataSource
import com.example.appsaba.data.local.entity.ServiceEntity
import com.example.appsaba.data.mapper.toDomain
import com.example.appsaba.data.mapper.toEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class ServiceRepositoryImpl @Inject constructor(
    private val source: ServiceLocalDataSource
) : ServiceRepository {
    override fun observeByParking(parkingId: Long) = source.observeByParking(parkingId).map { it.map(ServiceEntity::toDomain) }
    override fun observeById(serviceId: Long) = source.observeById(serviceId).map { it?.toDomain() }
    override suspend fun getById(serviceId: Long) = source.getById(serviceId)?.toDomain()
    override suspend fun insert(service: Service) = source.insert(service.toEntity())
    override suspend fun update(service: Service) = source.update(service.toEntity())
    override suspend fun delete(service: Service) = source.delete(service.toEntity())
    override suspend fun updateStatus(serviceId: Long, status: String, problem: String?) = source.updateStatus(serviceId, status, problem, System.currentTimeMillis())
}
