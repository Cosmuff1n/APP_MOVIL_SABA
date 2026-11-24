package com.example.appsaba.data.remote.apiservice

import com.example.appsaba.data.remote.dto.Ec2ParkingDto
import com.example.appsaba.data.remote.dto.Ec2ParkingCreateDto
import com.example.appsaba.data.remote.dto.Ec2ParkingPatchDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface Ec2ParkingApiService {
    @GET("api/parking")
    suspend fun getParkings(@Header("Authorization") authorization: String): List<Ec2ParkingDto>

    @POST("api/parking")
    suspend fun createParking(
        @Header("Authorization") authorization: String,
        @Body body: Ec2ParkingCreateDto
    ): Ec2ParkingDto

    @PATCH("api/parking/{id}")
    suspend fun patchParking(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
        @Body body: Ec2ParkingPatchDto
    ): Ec2ParkingDto

    @DELETE("api/parking/{id}")
    suspend fun deleteParking(
        @Header("Authorization") authorization: String,
        @Header("If-Match") revision: String,
        @Path("id") id: Long
    ): retrofit2.Response<Unit>
}
