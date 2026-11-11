package com.example.appsaba.ui.maintenance
import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.Service

data class MaintenanceUiState(
    val parkings: List<Parking> = emptyList(),
    val selectedParkingId: Long? = null,
    val services: List<Service> = emptyList(),
    val busy: Boolean = false,
    val feedback: String? = null
)
