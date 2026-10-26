package com.example.appsaba.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Caché independiente. Nunca mezcla IDs remotos con el Parking local existente. */
@Entity(tableName = "remote_parkings")
data class RemoteParking(
    @PrimaryKey val id: Long,
    val name: String,
    val address: String,
    val commune: String,
    val totalSpaces: Int,
    val freeSpaces: Int,
    val isActive: Boolean,
    val revision: Long,
    val updatedAt: Long,
    val lastSyncedAt: Long
)
