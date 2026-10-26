package com.example.appsaba.data.remote

import com.example.appsaba.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Cliente dedicado al EC2 de Saba. No cambia el Retrofit del login existente. */
object Ec2RetrofitClient {
    val api: Ec2ParkingApi by lazy {
        val baseUrl = BuildConfig.EC2_API_BASE_URL.trim().let {
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
            .create(Ec2ParkingApi::class.java)
    }
}
