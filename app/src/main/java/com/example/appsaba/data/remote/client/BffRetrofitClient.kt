package com.example.appsaba.data.remote.client

import com.example.appsaba.BuildConfig
import com.example.appsaba.data.remote.apiservice.BffIncidentApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Incidencias usa el mismo BFF configurado para Parking, no se conecta a 8082 directamente. */
object BffRetrofitClient {
    private val retrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(BuildConfig.BFF_API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val incidentApi: BffIncidentApiService by lazy {
        retrofit.create(BffIncidentApiService::class.java)
    }
}
