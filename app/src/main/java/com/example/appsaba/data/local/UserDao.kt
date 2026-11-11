package com.example.appsaba.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appsaba.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/** Acceso a perfiles locales; NO autentica ni guarda contrasenas. */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity): Int

    @Delete
    suspend fun delete(user: UserEntity): Int

    @Query("SELECT * FROM users ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE rut = :rut LIMIT 1")
    suspend fun getByRut(rut: String): UserEntity?

    @Query("SELECT * FROM users WHERE assignedParkingId = :parkingId ORDER BY name COLLATE NOCASE ASC")
    fun observeByParking(parkingId: Long): Flow<List<UserEntity>>
}
