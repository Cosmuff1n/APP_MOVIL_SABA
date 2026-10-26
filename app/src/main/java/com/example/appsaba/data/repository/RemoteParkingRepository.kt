package com.example.appsaba.data.repository

import com.example.appsaba.data.local.RemoteParkingDao
import com.example.appsaba.data.model.RemoteParking
import com.example.appsaba.data.remote.Ec2ParkingApi
import com.example.appsaba.data.remote.Ec2ParkingCreateDto
import com.example.appsaba.data.remote.Ec2ParkingDto
import com.example.appsaba.data.remote.Ec2ParkingPatchDto

/** EC2 es el origen de los datos remotos; Room guarda la última lectura correcta. */
class RemoteParkingRepository(
    private val api: Ec2ParkingApi,
    private val dao: RemoteParkingDao
) {
    private fun bearer(token: String) = "Bearer $token"

    suspend fun refresh(token: String) {
        val remote = api.getParkings(bearer(token))
        val now = System.currentTimeMillis()
        dao.replaceAll(remote.map { item ->
            RemoteParking(
                id = item.id,
                name = item.name,
                address = item.address,
                commune = item.commune,
                totalSpaces = item.totalSpaces,
                freeSpaces = item.freeSpaces,
                isActive = item.isActive,
                revision = item.revision,
                updatedAt = item.updatedAt,
                lastSyncedAt = now
            )
        })
    }

    suspend fun create(token: String, name: String) {
        api.createParking(bearer(token), Ec2ParkingCreateDto(name = name.trim()))
        refresh(token)
    }

    suspend fun rename(token: String, parking: RemoteParking, name: String) {
        api.patchParking(bearer(token), parking.id,
            Ec2ParkingPatchDto(revision = parking.revision, name = name.trim()))
        refresh(token)
    }

    suspend fun toggle(token: String, parking: RemoteParking) {
        api.patchParking(bearer(token), parking.id,
            Ec2ParkingPatchDto(revision = parking.revision, isActive = !parking.isActive))
        refresh(token)
    }

    suspend fun delete(token: String, parking: RemoteParking) {
        val response = api.deleteParking(bearer(token), parking.revision.toString(), parking.id)
        if (!response.isSuccessful) throw retrofit2.HttpException(response)
        refresh(token)
    }
}
