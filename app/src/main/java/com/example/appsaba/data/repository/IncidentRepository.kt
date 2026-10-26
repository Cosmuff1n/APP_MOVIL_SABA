package com.example.appsaba.data.repository

import com.example.appsaba.data.local.IncidentDao
import com.example.appsaba.data.local.ServiceDao
import com.example.appsaba.data.model.Incident
import kotlinx.coroutines.flow.Flow

/** Persistencia local de incidencias; verifica que el equipo pertenezca al parking. */
class IncidentRepository(
    private val incidentDao: IncidentDao,
    private val serviceDao: ServiceDao
) {
    fun observeByParking(parkingId: Long): Flow<List<Incident>> =
        incidentDao.observeByParking(parkingId)

    fun observeActiveByParking(parkingId: Long): Flow<List<Incident>> =
        incidentDao.observeActiveByParking(parkingId)

    fun observeByService(serviceId: Long): Flow<List<Incident>> =
        incidentDao.observeByService(serviceId)

    fun observeActiveCount(parkingId: Long): Flow<Int> =
        incidentDao.observeActiveCount(parkingId)

    suspend fun getById(incidentId: Long): Incident? =
        incidentDao.getById(incidentId)

    suspend fun insert(incident: Incident): Long {
        validateServiceParking(incident)
        return incidentDao.insert(incident)
    }

    suspend fun update(incident: Incident): Int {
        validateServiceParking(incident)
        return incidentDao.update(incident)
    }

    suspend fun delete(incident: Incident): Int = incidentDao.delete(incident)

    private suspend fun validateServiceParking(incident: Incident) {
        val service = serviceDao.getById(incident.serviceId)
            ?: throw IllegalArgumentException(
                "No existe el servicio con id ${incident.serviceId}."
            )
        require(service.parkingId == incident.parkingId) {
            "El servicio ${incident.serviceId} no pertenece al estacionamiento ${incident.parkingId}."
        }
    }
}
