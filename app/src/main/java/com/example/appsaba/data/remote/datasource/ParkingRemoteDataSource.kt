package com.example.appsaba.data.remote.datasource

import com.example.appsaba.data.remote.apiservice.Ec2ParkingApiService
import com.example.appsaba.data.remote.dto.Ec2ParkingCreateDto
import com.example.appsaba.data.remote.dto.Ec2ParkingDto
import com.example.appsaba.data.remote.dto.Ec2ParkingPatchDto
import retrofit2.HttpException
import javax.inject.Inject

interface ParkingRemoteDataSource {
    suspend fun list(token: String): List<Ec2ParkingDto>
    suspend fun create(token: String, name: String)
    suspend fun rename(token: String, id: Long, revision: Long, name: String)
    suspend fun toggle(token: String, id: Long, revision: Long, active: Boolean)
    suspend fun delete(token: String, id: Long, revision: Long)
}

class ParkingRemoteDataSourceImpl @Inject constructor(
    private val api: Ec2ParkingApiService
) : ParkingRemoteDataSource {
    private fun bearer(token: String) = "Bearer $token"
    override suspend fun list(token: String) = api.getParkings(bearer(token))
    override suspend fun create(token: String, name: String) {
        api.createParking(bearer(token), Ec2ParkingCreateDto(name = name.trim()))
    }
    override suspend fun rename(token: String, id: Long, revision: Long, name: String) {
        api.patchParking(bearer(token), id, Ec2ParkingPatchDto(revision, name = name.trim()))
    }
    override suspend fun toggle(token: String, id: Long, revision: Long, active: Boolean) {
        api.patchParking(bearer(token), id, Ec2ParkingPatchDto(revision, isActive = active))
    }
    override suspend fun delete(token: String, id: Long, revision: Long) {
        val response = api.deleteParking(bearer(token), revision.toString(), id)
        if (!response.isSuccessful) throw HttpException(response)
    }
}
