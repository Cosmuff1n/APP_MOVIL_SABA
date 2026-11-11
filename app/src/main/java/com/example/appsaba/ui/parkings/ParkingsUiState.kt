package com.example.appsaba.ui.parkings

import com.example.appsaba.domain.model.Parking

data class ParkingsUiState(
    val parkings: List<Parking> = emptyList(),
    val busy: Boolean = false,
    val feedback: String? = null
)
