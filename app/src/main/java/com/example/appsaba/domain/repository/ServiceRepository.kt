package com.example.appsaba.domain.repository

import com.example.appsaba.domain.model.Service
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun observeByParking(parkingId: Long): Flow<List<Service>>
    fun observeById(serviceId: Long): Flow<Service?>
    suspend fun getById(serviceId: Long): Service?
    suspend fun insert(service: Service): Long
    suspend fun update(service: Service): Int
    suspend fun delete(service: Service): Int
    suspend fun updateStatus(serviceId: Long, status: String, problem: String?): Int
}
