package com.example.appsaba.data.repository

import com.example.appsaba.data.local.UserDao
import com.example.appsaba.data.model.User
import kotlinx.coroutines.flow.Flow

/** Perfiles locales de trabajadores. No realiza autenticacion. */
class UserRepository(private val userDao: UserDao) {

    fun observeAll(): Flow<List<User>> = userDao.observeAll()

    fun observeByParking(parkingId: Long): Flow<List<User>> =
        userDao.observeByParking(parkingId)

    suspend fun getById(userId: Long): User? = userDao.getById(userId)

    suspend fun getByRut(rut: String): User? = userDao.getByRut(rut)

    suspend fun insert(user: User): Long = userDao.insert(user)

    suspend fun update(user: User): Int = userDao.update(user)

    suspend fun delete(user: User): Int = userDao.delete(user)
}
