package com.example.appsaba.data.mapper

import com.example.appsaba.domain.model.User
import com.example.appsaba.data.local.entity.UserEntity

/** Conversión explícita: Domain no conoce las anotaciones de Room. */
fun UserEntity.toDomain(): User = User(id = id, name = name, rut = rut, role = role, isActive = isActive, assignedParkingId = assignedParkingId, cognitoSub = cognitoSub)
fun User.toEntity(): UserEntity = UserEntity(id = id, name = name, rut = rut, role = role, isActive = isActive, assignedParkingId = assignedParkingId, cognitoSub = cognitoSub)
