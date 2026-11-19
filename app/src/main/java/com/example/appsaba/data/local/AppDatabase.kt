package com.example.appsaba.data.local

import com.example.appsaba.data.local.dao.ParkingDao
import com.example.appsaba.data.local.dao.IncidentDao
import com.example.appsaba.data.local.dao.UserDao
import com.example.appsaba.data.local.dao.ServiceDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appsaba.data.local.entity.IncidentEntity
import com.example.appsaba.data.local.entity.ParkingEntity
import com.example.appsaba.data.local.entity.ServiceEntity
import com.example.appsaba.data.local.entity.UserEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Una base SQLite local para las cuatro Entities de Saba. */
@Database(
    entities = [
        UserEntity::class,
        ParkingEntity::class,
        ServiceEntity::class,
        IncidentEntity::class
    ],
    version = 3,
    exportSchema = false // MVP: pendiente configurar exportacion y migraciones versionadas
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun parkingDao(): ParkingDao
    abstract fun serviceDao(): ServiceDao
    abstract fun incidentDao(): IncidentDao

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

        /**
         * v2 -> v3: trasladar la cache EC2 a `parkings`, nunca mezclar claves
         * remotas con las locales. Se preservan servicios/incidentes/usuarios.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE parkings ADD COLUMN remoteId INTEGER")
                db.execSQL("ALTER TABLE parkings ADD COLUMN revision INTEGER")
                db.execSQL("ALTER TABLE parkings ADD COLUMN remoteUpdatedAt INTEGER")
                db.execSQL("ALTER TABLE parkings ADD COLUMN lastSyncedAt INTEGER")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_parkings_remoteId ON parkings (remoteId)")
                // SQLite local autoincrement generates positive IDs.
                // The remote cache uses -(remoteId+1) to avoid collisions.
                db.execSQL("""INSERT OR IGNORE INTO parkings
                    (id, name, address, commune, totalSpaces, freeSpaces, isActive,
                     remoteId, revision, remoteUpdatedAt, lastSyncedAt)
                    SELECT -(id+1), name, address, commune, totalSpaces, freeSpaces, isActive,
                           id, revision, updatedAt, lastSyncedAt
                    FROM remote_parkings WHERE id >= 0 AND id < 9223372036854775807""".trimIndent())
                db.execSQL("DROP TABLE IF EXISTS remote_parkings")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "saba_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { database -> INSTANCE = database }
            }
        }
    }
}
