package com.example.appsaba.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appsaba.data.model.Incident
import com.example.appsaba.data.model.Parking
import com.example.appsaba.data.model.Service
import com.example.appsaba.data.model.User

/** Una base SQLite local para las cuatro Entities de Saba. */
@Database(
    entities = [
        User::class,
        Parking::class,
        Service::class,
        Incident::class
    ],
    version = 1,
    exportSchema = false // MVP: pendiente configurar exportacion y migraciones versionadas
)
abstract class SabaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun parkingDao(): ParkingDao
    abstract fun serviceDao(): ServiceDao
    abstract fun incidentDao(): IncidentDao

    companion object {
        @Volatile
        private var INSTANCE: SabaDatabase? = null

        fun getInstance(context: Context): SabaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SabaDatabase::class.java,
                    "saba_database"
                ).build().also { database -> INSTANCE = database }
            }
        }
    }
}
