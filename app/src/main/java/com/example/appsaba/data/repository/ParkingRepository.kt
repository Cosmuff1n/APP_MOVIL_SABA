package com.example.appsaba.data.repository

import com.example.appsaba.data.local.ParkingDao
import com.example.appsaba.data.model.Parking
import kotlinx.coroutines.flow.Flow

/** Consulta y administra estacionamientos guardados en Room. */
class ParkingRepository(private val parkingDao: ParkingDao) {

    fun observeAll(): Flow<List<Parking>> = parkingDao.observeAll()

    fun observeActive(): Flow<List<Parking>> = parkingDao.observeActive()

    suspend fun getById(parkingId: Long): Parking? = parkingDao.getById(parkingId)

    suspend fun insert(parking: Parking): Long = parkingDao.insert(parking)

    suspend fun update(parking: Parking): Int = parkingDao.update(parking)

    suspend fun delete(parking: Parking): Int = parkingDao.delete(parking)
}
