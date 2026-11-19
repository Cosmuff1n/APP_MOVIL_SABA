package com.example.appsaba.di

import com.example.appsaba.data.repository.*
import com.example.appsaba.data.local.datasource.*
import com.example.appsaba.data.remote.datasource.*
import com.example.appsaba.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindParkingLocalDataSource(impl: ParkingLocalDataSourceImpl): ParkingLocalDataSource
    @Binds
    abstract fun bindServiceLocalDataSource(impl: ServiceLocalDataSourceImpl): ServiceLocalDataSource
    @Binds
    abstract fun bindIncidentLocalDataSource(impl: IncidentLocalDataSourceImpl): IncidentLocalDataSource
    @Binds
    abstract fun bindUserLocalDataSource(impl: UserLocalDataSourceImpl): UserLocalDataSource
    @Binds
    abstract fun bindParkingRemoteDataSource(impl: ParkingRemoteDataSourceImpl): ParkingRemoteDataSource
    @Binds
    abstract fun bindParkingRepository(impl: ParkingRepositoryImpl): ParkingRepository
    @Binds
    abstract fun bindServiceRepository(impl: ServiceRepositoryImpl): ServiceRepository
    @Binds
    abstract fun bindIncidentRepository(impl: IncidentRepositoryImpl): IncidentRepository
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
    @Binds
    abstract fun bindLoginRepository(impl: LoginRepositoryImpl): LoginRepository
}
