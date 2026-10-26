package com.example.appsaba.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Ticket de falla sobre un servicio de un estacionamiento.
 *
 * evidenceUri apunta a una foto/video guardado por la app; no contiene
 * archivos binarios. La app debera conservar el acceso a dicho URI.
 * La coherencia serviceId -> parkingId se validara en Repository.
 */
@Entity(
    tableName = "incidents",
    foreignKeys = [
        ForeignKey(
            entity = Parking::class,
            parentColumns = ["id"],
            childColumns = ["parkingId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Service::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["reporterUserId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["resolvedByUserId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["ticketCode"], unique = true),
        Index(value = ["parkingId"]),
        Index(value = ["serviceId"]),
        Index(value = ["reporterUserId"]),
        Index(value = ["resolvedByUserId"])
    ]
)
data class Incident(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val ticketCode: String,
    val parkingId: Long,
    val serviceId: Long,
    val reporterUserId: Long? = null,
    val reporterName: String = "",
    val failureType: String,
    val description: String,
    val status: String = "degradado",
    val reportedAt: Long = System.currentTimeMillis(),
    val evidenceUri: String? = null,
    val evidenceFileName: String? = null,
    val evidenceMimeType: String? = null,
    val isResolved: Boolean = false,
    val resolvedByUserId: Long? = null,
    val resolvedByName: String? = null,
    val resolutionDescription: String? = null,
    val resolvedAt: Long? = null
)
