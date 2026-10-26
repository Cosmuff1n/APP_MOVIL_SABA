package com.example.appsaba.data.remote

/** Modelo remoto, distinto de la Entity Parking que ya usa la app. */
data class Ec2ParkingDto(
    val id: Long = 0,
    val name: String = "",
    val address: String = "",
    val commune: String = "",
    val totalSpaces: Int = 0,
    val freeSpaces: Int = 0,
    val isActive: Boolean = true,
    val revision: Long = 1,
    val updatedAt: Long = 0,
    val status: String = "Activo"
)
data class Ec2ParkingCreateDto(
    val name: String,
    val address: String = "",
    val commune: String = "",
    val totalSpaces: Int = 0,
    val freeSpaces: Int = 0,
    val isActive: Boolean = true
)
data class Ec2ParkingPatchDto(
    val revision: Long,
    val name: String? = null,
    val isActive: Boolean? = null
)
