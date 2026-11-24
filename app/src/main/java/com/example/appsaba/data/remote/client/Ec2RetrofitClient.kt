package com.example.appsaba.data.remote.client

import com.example.appsaba.data.remote.apiservice.Ec2ParkingApiService
import com.example.appsaba.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Cliente Retrofit del BFF; el API de login conserva su propia configuración. */
object Ec2RetrofitClient {
    val api: Ec2ParkingApiService by lazy {
        val baseUrl = BuildConfig.BFF_API_BASE_URL.trim().let {
            if (it.endsWith("/")) it else "$it/"
        }
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Ec2ParkingApiService::class.java)
    }
}
