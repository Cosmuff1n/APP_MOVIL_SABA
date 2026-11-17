package com.example.appsaba.ui.home

import com.example.appsaba.core.jwt.SessionUser
import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.model.Service

data class HomeUiState(
    val user: SessionUser? = null,
    val decodeError: String? = null,
    val parkings: List<Parking> = emptyList(),
    val selectedParkingId: Long? = null,
    val services: List<Service> = emptyList()
) {
    val selectedParking: Parking? get() = parkings.firstOrNull { it.id == selectedParkingId }
    val hasOutage: Boolean get() = services.any { it.status == "fuera-servicio" }
    val hasDegraded: Boolean get() = services.any { it.status == "degradado" }
}