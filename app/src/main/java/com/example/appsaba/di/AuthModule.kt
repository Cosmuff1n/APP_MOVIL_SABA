package com.example.appsaba.di

import android.content.Context
import com.example.appsaba.BuildConfig
import com.example.appsaba.auth.CognitoAuthManager
import com.example.appsaba.core.config.cognitoConfigFromBuild
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import com.example.appsaba.data.remote.apiservice.AuthApiService
import com.example.appsaba.data.remote.apiservice.FakeAuthApiService
import com.example.appsaba.data.remote.client.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides @Singleton
    fun provideCognitoAuthManager(@ApplicationContext context: Context): CognitoAuthManager =
        CognitoAuthManager(context, cognitoConfigFromBuild())

    @Provides fun provideAuthApi(): AuthApiService =
        if (BuildConfig.DEBUG) FakeAuthApiService() else RetrofitClient.authApi
}
