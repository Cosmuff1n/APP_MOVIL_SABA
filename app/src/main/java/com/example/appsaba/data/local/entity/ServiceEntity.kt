package com.example.appsaba.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Equipo o servicio tecnologico monitoreado dentro de un estacionamiento.
 * Tipos iniciales: barriers, lpr, pos, atm (segun Figma).
 * Estados: operativo, degradado, fuera-servicio.
 */
@Entity(
    tableName = "services",
    foreignKeys = [
        ForeignKey(
            entity = ParkingEntity::class,
            parentColumns = ["id"],
            childColumns = ["parkingId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["parkingId"])]
)
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val parkingId: Long,
    val name: String,
    val type: String,
    val detail: String = "",
    val status: String = "operativo",
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val problem: String? = null
)
