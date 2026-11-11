package com.example.appsaba.di

import com.example.appsaba.BuildConfig
import com.example.appsaba.data.remote.AuthApiService
import com.example.appsaba.data.remote.FakeAuthApiService
import com.example.appsaba.data.remote.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides fun provideAuthApi(): AuthApiService =
        if (BuildConfig.DEBUG) FakeAuthApiService() else RetrofitClient.authApi
}
