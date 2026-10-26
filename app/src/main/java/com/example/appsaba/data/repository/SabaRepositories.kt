package com.example.appsaba.data.repository

import android.content.Context
import com.example.appsaba.data.local.SabaDatabase

/** Punto central para obtener los repositories a partir de una unica Room Database. */
class SabaRepositories(context: Context) {
    private val database = SabaDatabase.getInstance(context.applicationContext)

    val userRepository: UserRepository = UserRepository(database.userDao())
    val parkingRepository: ParkingRepository = ParkingRepository(database.parkingDao())
    val serviceRepository: ServiceRepository = ServiceRepository(database.serviceDao())
    val incidentRepository: IncidentRepository = IncidentRepository(
        database.incidentDao(),
        database.serviceDao()
    )
}
