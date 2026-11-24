package com.example.appsaba.data.repository

import com.example.appsaba.domain.model.Incident
import com.example.appsaba.data.remote.datasource.IncidentRemoteDataSource
import com.example.appsaba.data.remote.dto.BffIncidentDto
import java.util.UUID
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
    private val services: ServiceRepository,
    private val remote: IncidentRemoteDataSource
) : IncidentRepository {
    override fun observeByParking(parkingId: Long) = source.observeByParking(parkingId).map { it.map(IncidentEntity::toDomain) }
    override fun observeActiveByParking(parkingId: Long) = source.observeActiveByParking(parkingId).map { it.map(IncidentEntity::toDomain) }
    override fun observeByService(serviceId: Long) = source.observeByService(serviceId).map { it.map(IncidentEntity::toDomain) }
    override fun observeActiveCount(parkingId: Long) = source.observeActiveCount(parkingId)
    override suspend fun getById(incidentId: Long) = source.getById(incidentId)?.toDomain()
    override suspend fun insert(incident: Incident): Long {
        require(incident.remoteId == null) { "Incidencia remota no se guarda como local sin mapeo seguro" }
        validateService(incident)
        return source.insert(incident.toEntity())
    }
    override suspend fun update(incident: Incident): Int {
        require(incident.remoteId == null) { "No edites incidencias remotas mediante Room" }
        validateService(incident)
        return source.update(incident.toEntity())
    }
    override suspend fun delete(incident: Incident): Int {
        require(incident.remoteId == null) { "Borrado remoto no soportado" }
        return source.delete(incident.toEntity())
    }
    override suspend fun listRemote(token: String): List<Incident> =
        remote.list(token).map { it.toDomain() }

    override suspend fun createRemote(token: String, parkingId: Long, serviceId: Long,
                                      reporterName: String, failureType: String,
                                      description: String, status: String): Incident {
        // parkingId es el ID positivo REMOTO que asigna Spring, nunca Parking.id de SQLite.
        require(parkingId > 0 && serviceId > 0) { "Usa IDs remotos positivos para parking y service" }
        require(failureType.isNotBlank() && description.isNotBlank()) { "Falla y descripción obligatorias" }
        require(description.length <= 240 && failureType.length <= 120) { "Texto demasiado largo" }
        require(status == "degradado" || status == "fuera-servicio") { "Estado remoto inválido" }
        val request = BffIncidentDto(
            ticketCode = "SABA-${UUID.randomUUID().toString().take(8).uppercase()}",
            parkingId = parkingId, serviceId = serviceId, reporterName = reporterName.trim(),
            failureType = failureType.trim(), description = description.trim(), status = status
        )
        return remote.create(token, request).toDomain()
    }

    override suspend fun resolveRemote(token: String, incident: Incident,
                                       resolvedByName: String, resolutionDescription: String): Incident {
        require(!incident.isResolved) { "La incidencia ya está resuelta" }
        require(resolvedByName.isNotBlank() && resolutionDescription.isNotBlank()) { "Completa la resolución" }
        // PUT del backend exige failureType, description, status e isResolved.
        val request = BffIncidentDto(
            failureType = incident.failureType,
            description = incident.description,
            status = incident.status,
            isResolved = true,
            resolvedByName = resolvedByName.trim(),
            resolutionDescription = resolutionDescription.trim()
        )
        return remote.update(token, requireNotNull(incident.remoteId) { "No es una incidencia remota" }, request).toDomain()
    }

    private suspend fun validateService(incident: Incident) {
        val service = services.getById(incident.serviceId)
            ?: throw IllegalArgumentException("No existe el equipo con id ${incident.serviceId}")
        require(service.parkingId == incident.parkingId) {
            "El equipo ${incident.serviceId} no pertenece al parking ${incident.parkingId}"
        }
    }
}
