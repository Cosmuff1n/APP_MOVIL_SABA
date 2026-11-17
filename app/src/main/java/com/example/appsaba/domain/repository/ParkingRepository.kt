package com.example.appsaba.domain.repository

import com.example.appsaba.domain.model.Parking
import kotlinx.coroutines.flow.Flow

interface ParkingRepository {
    fun observeAll(): Flow<List<Parking>>
    fun observeActive(): Flow<List<Parking>>
    suspend fun getById(parkingId: Long): Parking?
    suspend fun insert(parking: Parking): Long
    suspend fun update(parking: Parking): Int
    suspend fun delete(parking: Parking): Int
}
