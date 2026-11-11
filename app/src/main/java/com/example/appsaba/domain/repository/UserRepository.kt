package com.example.appsaba.domain.repository

import com.example.appsaba.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeAll(): Flow<List<User>>
    fun observeByParking(parkingId: Long): Flow<List<User>>
    suspend fun getById(userId: Long): User?
    suspend fun getByRut(rut: String): User?
    suspend fun insert(user: User): Long
    suspend fun update(user: User): Int
    suspend fun delete(user: User): Int
}
