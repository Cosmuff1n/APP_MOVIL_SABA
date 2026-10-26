package com.example.appsaba.data.repository

import com.example.appsaba.data.local.ServiceDao
import com.example.appsaba.data.model.Service
import kotlinx.coroutines.flow.Flow

/** Estado operativo de los equipos de cada estacionamiento. */
class ServiceRepository(private val serviceDao: ServiceDao) {

    fun observeByParking(parkingId: Long): Flow<List<Service>> =
        serviceDao.observeByParking(parkingId)

    fun observeById(serviceId: Long): Flow<Service?> =
        serviceDao.observeById(serviceId)

    suspend fun getById(serviceId: Long): Service? = serviceDao.getById(serviceId)

    suspend fun insert(service: Service): Long = serviceDao.insert(service)

    suspend fun update(service: Service): Int = serviceDao.update(service)

    suspend fun delete(service: Service): Int = serviceDao.delete(service)

    /** Devuelve 1 si se actualizo el equipo, o 0 si no existe. */
    suspend fun updateStatus(
        serviceId: Long,
        status: String,
        problem: String? = null,
        updatedAt: Long = System.currentTimeMillis()
    ): Int = serviceDao.updateStatus(serviceId, status, problem, updatedAt)
}
