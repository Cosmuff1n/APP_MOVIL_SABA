package com.example.appsaba.di

import com.example.appsaba.data.local.AppDatabase
import com.example.appsaba.data.local.dao.ParkingDao
import com.example.appsaba.data.local.dao.IncidentDao
import com.example.appsaba.data.local.dao.UserDao
import com.example.appsaba.data.local.dao.ServiceDao
import android.content.Context
import com.example.appsaba.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /** Usa el singleton con MIGRATION_1_2. No reinicia ni borra las tablas previas. */
    @Provides @Singleton fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)
    @Provides fun provideParkingDao(db: AppDatabase): ParkingDao = db.parkingDao()
    @Provides fun provideServiceDao(db: AppDatabase): ServiceDao = db.serviceDao()
    @Provides fun provideIncidentDao(db: AppDatabase): IncidentDao = db.incidentDao()
    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}
