package com.example.appsaba.data.remote.datasource

import com.example.appsaba.data.remote.apiservice.BffIncidentApiService
import com.example.appsaba.data.remote.dto.BffIncidentDto
import javax.inject.Inject

interface IncidentRemoteDataSource {
    suspend fun list(token: String): List<BffIncidentDto>
    suspend fun create(token: String, incident: BffIncidentDto): BffIncidentDto
    suspend fun update(token: String, id: Long, incident: BffIncidentDto): BffIncidentDto
}

class IncidentRemoteDataSourceImpl @Inject constructor(
    private val api: BffIncidentApiService
) : IncidentRemoteDataSource {
    private fun authorization(token: String) = "Bearer $token"

    override suspend fun list(token: String) = api.list(authorization(token))

    override suspend fun create(token: String, incident: BffIncidentDto) =
        api.create(authorization(token), incident)

    override suspend fun update(token: String, id: Long, incident: BffIncidentDto) =
        api.update(authorization(token), id, incident)
}
