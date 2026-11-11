package com.example.appsaba.data.repository

import com.example.appsaba.domain.model.Incident
import com.example.appsaba.domain.repository.IncidentRepository
import com.example.appsaba.domain.repository.ServiceRepository
import com.example.appsaba.data.local.datasource.IncidentLocalDataSource
import com.example.appsaba.data.local.entity.IncidentEntity
import com.example.appsaba.data.mapper.toDomain
import com.example.appsaba.data.mapper.toEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class IncidentRepositoryImpl @Inject constructor(
    private val source: IncidentLocalDataSource,
    private val services: ServiceRepository
) : IncidentRepository {
    override fun observeByParking(parkingId: Long) = source.observeByParking(parkingId).map { it.map(IncidentEntity::toDomain) }
    override fun observeActiveByParking(parkingId: Long) = source.observeActiveByParking(parkingId).map { it.map(IncidentEntity::toDomain) }
    override fun observeByService(serviceId: Long) = source.observeByService(serviceId).map { it.map(IncidentEntity::toDomain) }
    override fun observeActiveCount(parkingId: Long) = source.observeActiveCount(parkingId)
    override suspend fun getById(incidentId: Long) = source.getById(incidentId)?.toDomain()
    override suspend fun insert(incident: Incident): Long {
        validateService(incident)
        return source.insert(incident.toEntity())
    }
    override suspend fun update(incident: Incident): Int {
        validateService(incident)
        return source.update(incident.toEntity())
    }
    override suspend fun delete(incident: Incident) = source.delete(incident.toEntity())
    private suspend fun validateService(incident: Incident) {
        val service = services.getById(incident.serviceId)
            ?: throw IllegalArgumentException("No existe el equipo con id ${incident.serviceId}")
        require(service.parkingId == incident.parkingId) {
            "El equipo ${incident.serviceId} no pertenece al parking ${incident.parkingId}"
        }
    }
}
