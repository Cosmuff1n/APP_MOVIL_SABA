package com.example.appsaba.data.remote

import retrofit2.http.GET

interface Ec2ParkingApi {
    @GET("api/parkings")
    suspend fun getParkings(): List<Ec2ParkingDto>
}
