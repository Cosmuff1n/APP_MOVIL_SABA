package com.example.appsaba.data.repository

import com.example.appsaba.domain.model.Parking
import com.example.appsaba.domain.repository.ParkingRepository
import com.example.appsaba.data.local.datasource.ParkingLocalDataSource
import com.example.appsaba.data.local.entity.ParkingEntity
import com.example.appsaba.data.remote.datasource.ParkingRemoteDataSource
import com.example.appsaba.data.mapper.toDomain
import com.example.appsaba.data.mapper.toEntity
import com.example.appsaba.data.mapper.toCachedEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.map

/** Un solo repositorio para datos locales y cache de parkings remotos del BFF. */
class ParkingRepositoryImpl @Inject constructor(
    private val source: ParkingLocalDataSource,
    private val remote: ParkingRemoteDataSource
) : ParkingRepository {
    override fun observeAll() = source.observeAll().map { it.map(ParkingEntity::toDomain) }
    override fun observeActive() = source.observeActive().map { it.map(ParkingEntity::toDomain) }
    override fun observeRemote() = source.observeRemote().map { it.map(ParkingEntity::toDomain) }
    override suspend fun getById(parkingId: Long) = source.getById(parkingId)?.toDomain()
    override suspend fun insert(parking: Parking) = source.insert(parking.toEntity())
    override suspend fun update(parking: Parking) = source.update(parking.toEntity())
    override suspend fun delete(parking: Parking) = source.delete(parking.toEntity())

    override suspend fun refreshRemote(token: String) {
        val now = System.currentTimeMillis()
        remote.list(token).forEach { dto -> source.upsertRemote(dto.toCachedEntity(now)) }
        // Do not delete cached rows: incidents/services may reference them via FKs.
    }
    override suspend fun createRemote(token: String, name: String) {
        remote.create(token, name); refreshRemote(token)
    }
    override suspend fun renameRemote(token: String, parking: Parking, name: String) {
        remote.rename(token, remoteId(parking), revision(parking), name); refreshRemote(token)
    }
    override suspend fun toggleRemote(token: String, parking: Parking) {
        remote.toggle(token, remoteId(parking), revision(parking), !parking.isActive); refreshRemote(token)
    }
    override suspend fun deleteRemote(token: String, parking: Parking) {
        remote.delete(token, remoteId(parking), revision(parking))
        source.deleteCachedRemote(remoteId(parking))
        refreshRemote(token)
    }
    private fun remoteId(parking: Parking): Long = requireNotNull(parking.remoteId) { "Parking no remoto" }
    private fun revision(parking: Parking): Long = requireNotNull(parking.revision) { "Falta revision remota" }
}
