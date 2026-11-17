package com.example.appsaba.ui.incidents

import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.Service
import com.example.appsaba.domain.model.Incident

data class IncidentsUiState(
    val parkings: List<Parking> = emptyList(),
    val selectedParkingId: Long? = null,
    val services: List<Service> = emptyList(),
    val incidents: List<Incident> = emptyList(),
    val isReportSheetVisible: Boolean = false,
    val busy: Boolean = false,
    val feedback: String? = null
)
