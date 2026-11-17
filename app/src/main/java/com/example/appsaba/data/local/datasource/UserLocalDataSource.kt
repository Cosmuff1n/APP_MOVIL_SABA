package com.example.appsaba.data.local.datasource

import com.example.appsaba.data.local.entity.UserEntity
import com.example.appsaba.data.local.UserDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    fun observeAll(): Flow<List<UserEntity>>
    fun observeByParking(parkingId: Long): Flow<List<UserEntity>>
    suspend fun getById(userId: Long): UserEntity?
    suspend fun getByRut(rut: String): UserEntity?
    suspend fun insert(user: UserEntity): Long
    suspend fun update(user: UserEntity): Int
    suspend fun delete(user: UserEntity): Int
}

class UserLocalDataSourceImpl @Inject constructor(
    private val dao: UserDao
) : UserLocalDataSource {
    override fun observeAll() = dao.observeAll()
    override fun observeByParking(parkingId: Long) = dao.observeByParking(parkingId)
    override suspend fun getById(userId: Long) = dao.getById(userId)
    override suspend fun getByRut(rut: String) = dao.getByRut(rut)
    override suspend fun insert(user: UserEntity) = dao.insert(user)
    override suspend fun update(user: UserEntity) = dao.update(user)
    override suspend fun delete(user: UserEntity) = dao.delete(user)
}
