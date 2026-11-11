package com.example.appsaba.di

import com.example.appsaba.data.local.datasource.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds abstract fun parking(impl: ParkingLocalDataSourceImpl): ParkingLocalDataSource
    @Binds abstract fun service(impl: ServiceLocalDataSourceImpl): ServiceLocalDataSource
    @Binds abstract fun incident(impl: IncidentLocalDataSourceImpl): IncidentLocalDataSource
    @Binds abstract fun user(impl: UserLocalDataSourceImpl): UserLocalDataSource
}
