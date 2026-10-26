package com.example.appsaba.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Estacionamiento administrado desde Saba.
 *
 * La disponibilidad de plazas es diferente del estado de sus equipos
 * tecnologicos (que se guarda en Service).
 */
@Entity(tableName = "parkings")
data class Parking(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val address: String,
    val commune: String = "",
    val totalSpaces: Int = 0,
    val freeSpaces: Int = 0,
    val isActive: Boolean = true
)
