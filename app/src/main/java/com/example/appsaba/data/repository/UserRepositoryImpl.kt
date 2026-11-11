package com.example.appsaba.data.repository

import com.example.appsaba.domain.model.User
import com.example.appsaba.domain.repository.UserRepository
import com.example.appsaba.domain.repository.ServiceRepository
import com.example.appsaba.data.local.datasource.UserLocalDataSource
import com.example.appsaba.data.local.entity.UserEntity
import com.example.appsaba.data.mapper.toDomain
import com.example.appsaba.data.mapper.toEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class UserRepositoryImpl @Inject constructor(
    private val source: UserLocalDataSource
) : UserRepository {
    override fun observeAll() = source.observeAll().map { it.map(UserEntity::toDomain) }
    override fun observeByParking(parkingId: Long) = source.observeByParking(parkingId).map { it.map(UserEntity::toDomain) }
    override suspend fun getById(userId: Long) = source.getById(userId)?.toDomain()
    override suspend fun getByRut(rut: String) = source.getByRut(rut)?.toDomain()
    override suspend fun insert(user: User) = source.insert(user.toEntity())
    override suspend fun update(user: User) = source.update(user.toEntity())
    override suspend fun delete(user: User) = source.delete(user.toEntity())
}
