package com.example.appsaba

import com.example.appsaba.data.local.entity.ParkingEntity
import com.example.appsaba.data.mapper.toCachedEntity
import com.example.appsaba.data.mapper.toDomain
import com.example.appsaba.data.remote.dto.Ec2ParkingDto
import org.junit.Assert.*
import org.junit.Test

class ParkingMapperTest {
    @Test fun remoteIdDoesNotCollideWithLocalId() {
        val local = ParkingEntity(id = 1, name = "Local", address = "A")
        val remote = Ec2ParkingDto(id = 1, name = "Remoto", address = "B")
            .toCachedEntity(now = 1234)
        assertEquals(1L, local.id)
        assertEquals(-2L, remote.id)
        assertEquals(1L, remote.remoteId)
        assertEquals(1234L, remote.lastSyncedAt)
        assertNull(local.remoteId)
    }

    @Test fun mapperKeepsRemoteMetadata() {
        val remote = Ec2ParkingDto(id = 4, name = "Central", address = "C", revision = 8)
            .toCachedEntity(now = 999)
        val domain = remote.toDomain()
        assertEquals(-5L, domain.id)
        assertEquals(4L, domain.remoteId)
        assertEquals(8L, domain.revision)
        assertEquals(999L, domain.lastSyncedAt)
    }
}
