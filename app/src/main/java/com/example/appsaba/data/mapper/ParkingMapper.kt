package com.example.appsaba.data.mapper

import com.example.appsaba.domain.model.Parking
import com.example.appsaba.data.local.entity.ParkingEntity
import com.example.appsaba.data.remote.dto.Ec2ParkingDto

/** Mapeos entre modelos de negocio, fila Room y respuesta de EC2. */
fun ParkingEntity.toDomain(): Parking = Parking(
    id, name, address, commune, totalSpaces, freeSpaces, isActive,
    remoteId, revision, remoteUpdatedAt, lastSyncedAt
)
fun Parking.toEntity(): ParkingEntity = ParkingEntity(
    id, name, address, commune, totalSpaces, freeSpaces, isActive,
    remoteId, revision, remoteUpdatedAt, lastSyncedAt
)
fun Ec2ParkingDto.toCachedEntity(now: Long): ParkingEntity {
    require(id >= 0 && id < Long.MAX_VALUE) { "Identificador remoto invalido: $id" }
    return ParkingEntity(
        id = -(id + 1L), // Nunca colisiona con los IDs positivos locales.
        name = name, address = address, commune = commune,
        totalSpaces = totalSpaces, freeSpaces = freeSpaces, isActive = isActive,
        remoteId = id, revision = revision, remoteUpdatedAt = updatedAt, lastSyncedAt = now
    )
}
