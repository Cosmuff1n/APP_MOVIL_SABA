package com.example.appsaba.data.remote

/** Formato de /api/parkings en EC2. No confundir con la Entity Parking de Room. */
data class Ec2ParkingDto(
    val id: Long,
    val name: String,
    val status: String
)
