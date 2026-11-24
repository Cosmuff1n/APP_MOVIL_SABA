package com.example.appsaba.ui.incidents

import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.Service
import com.example.appsaba.domain.model.Incident

data class IncidentUiState(
    val parkings: List<Parking> = emptyList(),
    val selectedParkingId: Long? = null,
    val services: List<Service> = emptyList(),
    val incidents: List<Incident> = emptyList(),
    val isReportSheetVisible: Boolean = false,
    val busy: Boolean = false,
    val feedback: String? = null,
    /** Estado de la consulta remota dentro del mismo UiState. */
    val remoteBusy: Boolean = false,
    val remoteFeedback: String? = null,
    val remoteAvailable: Boolean = false
)



enum class EstadoEquipo { FUNCIONAL, DEGRADADO, FUERA_DE_SERVICIO}

data class Ticket(
    val id: String,
    val equipo: String,
    val tipoFalla: String,
    val estado: EstadoEquipo,
    val observaciones: String,
    val activo: Boolean = true,
    val isRemote: Boolean = false
)
