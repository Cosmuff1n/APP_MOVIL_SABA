package com.example.appsaba.domain.repository

import com.example.appsaba.domain.model.Parking
import kotlinx.coroutines.flow.Flow

interface ParkingRepository {
    fun observeAll(): Flow<List<Parking>>
    fun observeActive(): Flow<List<Parking>>
    fun observeRemote(): Flow<List<Parking>>
    suspend fun refreshRemote(token: String)
    suspend fun createRemote(token: String, name: String)
    suspend fun renameRemote(token: String, parking: Parking, name: String)
    suspend fun toggleRemote(token: String, parking: Parking)
    suspend fun deleteRemote(token: String, parking: Parking)
    suspend fun getById(parkingId: Long): Parking?
    suspend fun insert(parking: Parking): Long
    suspend fun update(parking: Parking): Int
    suspend fun delete(parking: Parking): Int
}
