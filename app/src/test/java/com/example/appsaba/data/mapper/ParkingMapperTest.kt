package com.example.appsaba.data.mapper

import com.example.appsaba.data.local.entity.ParkingEntity
import com.example.appsaba.data.remote.dto.Ec2ParkingDto
import com.example.appsaba.domain.model.Parking
import org.junit.Assert.*
import org.junit.Test

class ParkingMapperTest {
    @Test
    fun localEntityPreservesAllDomainFields() {
        val entity =
            ParkingEntity(8, "Norte", "Av. Uno", "Providencia", 80, 20, false, 55, 7, 100, 200)
        val domain = entity.toDomain()
        assertEquals(8L, domain.id)
        assertEquals("Norte", domain.name)
        assertEquals("Providencia", domain.commune)
        assertEquals(80, domain.totalSpaces)
        assertEquals(20, domain.freeSpaces)
        assertFalse(domain.isActive)
        assertEquals(55L, domain.remoteId)
        assertEquals(7L, domain.revision)
        assertEquals(entity, domain.toEntity())
    }

    @Test
    fun domainRoundtripPreservesRemoteMetadata() {
        val parking = Parking(3, "Centro", "A", "La Florida", 10, 2, true, 44, 5, 123, 789)
        assertEquals(parking, parking.toEntity().toDomain())
    }

    @Test
    fun remoteIdentityNeverCollidesWithLocalPositiveIds() {
        val row = Ec2ParkingDto(id = 20, name = "BFF").toCachedEntity(now = 123L)
        assertEquals(-21L, row.id)
        assertEquals(20L, row.remoteId)
        assertEquals(123L, row.lastSyncedAt)
    }

    @Test
    fun remoteZeroIdMapsToNegativeOne() {
        assertEquals(-1L, Ec2ParkingDto(id = 0).toCachedEntity(1).id)
    }

    @Test
    fun remoteInvalidNegativeIdRejected() {
        assertTrue(runCatching { Ec2ParkingDto(id = -1).toCachedEntity(1) }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun remoteMaxLongIdRejectedWithoutOverflow() {
        assertTrue(runCatching { Ec2ParkingDto(id = Long.MAX_VALUE).toCachedEntity(1) }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun remoteDtoPreservesRevisionAndAvailability() {
        val dto = Ec2ParkingDto(22, "Sur", "C", "Maipu", 40, 12, false, 13, 500)
        val entity = dto.toCachedEntity(999)
        assertEquals(13L, entity.revision)
        assertEquals(500L, entity.remoteUpdatedAt)
        assertEquals(40, entity.totalSpaces)
        assertEquals(12, entity.freeSpaces)
        assertFalse(entity.isActive)
    }
}
