package com.example.appsaba.data.local.datasource

import com.example.appsaba.data.local.entity.IncidentEntity
import com.example.appsaba.data.local.IncidentDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface IncidentLocalDataSource {
    fun observeByParking(parkingId: Long): Flow<List<IncidentEntity>>
    fun observeActiveByParking(parkingId: Long): Flow<List<IncidentEntity>>
    fun observeByService(serviceId: Long): Flow<List<IncidentEntity>>
    fun observeActiveCount(parkingId: Long): Flow<Int>
    suspend fun getById(incidentId: Long): IncidentEntity?
    suspend fun insert(incident: IncidentEntity): Long
    suspend fun update(incident: IncidentEntity): Int
    suspend fun delete(incident: IncidentEntity): Int
}

class IncidentLocalDataSourceImpl @Inject constructor(
    private val dao: IncidentDao
) : IncidentLocalDataSource {
    override fun observeByParking(parkingId: Long) = dao.observeByParking(parkingId)
    override fun observeActiveByParking(parkingId: Long) = dao.observeActiveByParking(parkingId)
    override fun observeByService(serviceId: Long) = dao.observeByService(serviceId)
    override fun observeActiveCount(parkingId: Long) = dao.observeActiveCount(parkingId)
    override suspend fun getById(incidentId: Long) = dao.getById(incidentId)
    override suspend fun insert(incident: IncidentEntity) = dao.insert(incident)
    override suspend fun update(incident: IncidentEntity) = dao.update(incident)
    override suspend fun delete(incident: IncidentEntity) = dao.delete(incident)
}
