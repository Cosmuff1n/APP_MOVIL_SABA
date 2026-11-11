package com.example.appsaba.data.mapper

import com.example.appsaba.domain.model.Service
import com.example.appsaba.data.local.entity.ServiceEntity

/** Conversión explícita: Domain no conoce las anotaciones de Room. */
fun ServiceEntity.toDomain(): Service = Service(id = id, parkingId = parkingId, name = name, type = type, detail = detail, status = status, lastUpdatedAt = lastUpdatedAt, problem = problem)
fun Service.toEntity(): ServiceEntity = ServiceEntity(id = id, parkingId = parkingId, name = name, type = type, detail = detail, status = status, lastUpdatedAt = lastUpdatedAt, problem = problem)
