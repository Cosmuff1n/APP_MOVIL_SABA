package com.example.appsaba.di

import com.example.appsaba.data.remote.apiservice.Ec2ParkingApiService
import com.example.appsaba.data.remote.client.Ec2RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideEc2ParkingApi(): Ec2ParkingApiService = Ec2RetrofitClient.api
}
