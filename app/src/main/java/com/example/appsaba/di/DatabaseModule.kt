package com.example.appsaba.di

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
    @Provides @Singleton fun provideDatabase(@ApplicationContext context: Context): SabaDatabase =
        SabaDatabase.getInstance(context)
    @Provides fun provideParkingDao(db: SabaDatabase): ParkingDao = db.parkingDao()
    @Provides fun provideServiceDao(db: SabaDatabase): ServiceDao = db.serviceDao()
    @Provides fun provideIncidentDao(db: SabaDatabase): IncidentDao = db.incidentDao()
    @Provides fun provideUserDao(db: SabaDatabase): UserDao = db.userDao()
}
