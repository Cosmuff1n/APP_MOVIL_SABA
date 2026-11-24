package com.example.appsaba.data.remote.apiservice

import com.example.appsaba.data.remote.dto.BffIncidentDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Las rutas coinciden con IncidentBffController del proyecto Spring Boot. */
interface BffIncidentApiService {
    @GET("api/incidents")
    suspend fun list(
        @Header("Authorization") authorization: String,
        @Query("parkingId") parkingId: Long? = null
    ): List<BffIncidentDto>

    @GET("api/incidents/{id}")
    suspend fun get(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): BffIncidentDto

    @POST("api/incidents")
    suspend fun create(
        @Header("Authorization") authorization: String,
        @Body incident: BffIncidentDto
    ): BffIncidentDto

    @PUT("api/incidents/{id}")
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
        @Body incident: BffIncidentDto
    ): BffIncidentDto
}
