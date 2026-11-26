package com.example.appsaba.data.mapper

import com.example.appsaba.domain.model.Service
import com.example.appsaba.data.local.entity.ServiceEntity
import org.junit.Assert.*
import org.junit.Test

class ServiceMapperTest {
    @Test
    fun roundtripPreservesServiceProperties() {
        val domain = Service(
            id = 2, parkingId = 3, name = "Barrera", type = "barrier",
            detail = "Entrada", status = "degradado", lastUpdatedAt = 111, problem = "Motor"
        )
        assertEquals(domain, domain.toEntity().toDomain())
    }

    @Test
    fun defaultStateAndNullableProblemRemainUnchanged() {
        val row = ServiceEntity(
            id = 9, parkingId = 1, name = "POS", type = "pos",
            status = "operativo", lastUpdatedAt = 1234, problem = null
        )
        assertNull(row.toDomain().problem)
        assertEquals("operativo", row.toDomain().status)
        assertEquals(row, row.toDomain().toEntity())
    }
}
