package com.example.appsaba.data.local.datasource

import com.example.appsaba.data.local.entity.ServiceEntity
import com.example.appsaba.data.local.ServiceDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface ServiceLocalDataSource {
    fun observeByParking(parkingId: Long): Flow<List<ServiceEntity>>
    fun observeById(serviceId: Long): Flow<ServiceEntity?>
    suspend fun getById(serviceId: Long): ServiceEntity?
    suspend fun insert(service: ServiceEntity): Long
    suspend fun update(service: ServiceEntity): Int
    suspend fun delete(service: ServiceEntity): Int
    suspend fun updateStatus(serviceId: Long, status: String, problem: String?, updatedAt: Long): Int
}

class ServiceLocalDataSourceImpl @Inject constructor(
    private val dao: ServiceDao
) : ServiceLocalDataSource {
    override fun observeByParking(parkingId: Long) = dao.observeByParking(parkingId)
    override fun observeById(serviceId: Long) = dao.observeById(serviceId)
    override suspend fun getById(serviceId: Long) = dao.getById(serviceId)
    override suspend fun insert(service: ServiceEntity) = dao.insert(service)
    override suspend fun update(service: ServiceEntity) = dao.update(service)
    override suspend fun delete(service: ServiceEntity) = dao.delete(service)
    override suspend fun updateStatus(serviceId: Long, status: String, problem: String?, updatedAt: Long) = dao.updateStatus(serviceId, status, problem, updatedAt)
}
