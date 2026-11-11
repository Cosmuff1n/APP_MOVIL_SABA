package com.example.appsaba.domain.repository

import com.example.appsaba.domain.model.Incident
import kotlinx.coroutines.flow.Flow

interface IncidentRepository {
    fun observeByParking(parkingId: Long): Flow<List<Incident>>
    fun observeActiveByParking(parkingId: Long): Flow<List<Incident>>
    fun observeByService(serviceId: Long): Flow<List<Incident>>
    fun observeActiveCount(parkingId: Long): Flow<Int>
    suspend fun getById(incidentId: Long): Incident?
    suspend fun insert(incident: Incident): Long
    suspend fun update(incident: Incident): Int
    suspend fun delete(incident: Incident): Int
}
