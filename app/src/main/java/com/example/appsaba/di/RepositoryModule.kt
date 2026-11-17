package com.example.appsaba.di

import com.example.appsaba.data.repository.*
import com.example.appsaba.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun parking(impl: ParkingRepositoryImpl): ParkingRepository
    @Binds abstract fun service(impl: ServiceRepositoryImpl): ServiceRepository
    @Binds abstract fun incident(impl: IncidentRepositoryImpl): IncidentRepository
    @Binds abstract fun user(impl: UserRepositoryImpl): UserRepository
    @Binds abstract fun login(impl: LoginRepositoryImpl): LoginRepository
}
