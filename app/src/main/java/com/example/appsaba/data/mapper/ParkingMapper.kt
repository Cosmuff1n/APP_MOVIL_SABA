package com.example.appsaba.data.mapper

import com.example.appsaba.domain.model.Parking
import com.example.appsaba.data.local.entity.ParkingEntity

/** Conversión explícita: Domain no conoce las anotaciones de Room. */
fun ParkingEntity.toDomain(): Parking = Parking(id = id, name = name, address = address, commune = commune, totalSpaces = totalSpaces, freeSpaces = freeSpaces, isActive = isActive)
fun Parking.toEntity(): ParkingEntity = ParkingEntity(id = id, name = name, address = address, commune = commune, totalSpaces = totalSpaces, freeSpaces = freeSpaces, isActive = isActive)
