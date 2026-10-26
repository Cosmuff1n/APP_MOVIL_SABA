package com.example.appsaba.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appsaba.data.model.Incident
import com.example.appsaba.data.model.Parking
import com.example.appsaba.data.model.Service
import com.example.appsaba.data.model.User
import com.example.appsaba.data.model.RemoteParking
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Una base SQLite local para las cuatro Entities de Saba. */
@Database(
    entities = [
        User::class,
        Parking::class,
        Service::class,
        Incident::class,
        RemoteParking::class
    ],
    version = 2,
    exportSchema = false // MVP: pendiente configurar exportacion y migraciones versionadas
)
abstract class SabaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun parkingDao(): ParkingDao
    abstract fun serviceDao(): ServiceDao
    abstract fun incidentDao(): IncidentDao
    abstract fun remoteParkingDao(): RemoteParkingDao

    companion object {
        // Migración incremental: crea una tabla nueva sin tocar parkings, services,
        // incidents ni users, que siguen siendo los datos locales originales.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS `remote_parkings` (
                    `id` INTEGER NOT NULL,
                    `name` TEXT NOT NULL,
                    `address` TEXT NOT NULL,
                    `commune` TEXT NOT NULL,
                    `totalSpaces` INTEGER NOT NULL,
                    `freeSpaces` INTEGER NOT NULL,
                    `isActive` INTEGER NOT NULL,
                    `revision` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    `lastSyncedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )""".trimIndent())
            }
        }

        @Volatile
        private var INSTANCE: SabaDatabase? = null

        fun getInstance(context: Context): SabaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SabaDatabase::class.java,
                    "saba_database"
                ).addMigrations(MIGRATION_1_2).build().also { database -> INSTANCE = database }
            }
        }
    }
}
