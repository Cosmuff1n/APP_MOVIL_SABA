package com.example.appsaba.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Perfil local de un trabajador de Saba. NO es una cuenta de autenticacion.
 * Nunca almacenar contrasenas ni tokens de sesion en esta tabla.
 *
 * assignedParkingId representa solo el estacionamiento PRINCIPAL.
 * Para multiples parkings hara falta una tabla intermedia mas adelante.
 */

@Entity(
    tableName = "users",
    foreignKeys = [
        ForeignKey(
            entity = Parking::class,
            parentColumns = ["id"],
            childColumns = ["assignedParkingId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["rut"], unique = true),
        Index(value = ["cognitoSub"], unique = true),
        Index(value = ["assignedParkingId"])
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val rut: String? = null,
    val role: String = "operador",
    val isActive: Boolean = true,
    val assignedParkingId: Long? = null,
    val cognitoSub: String? = null
)
