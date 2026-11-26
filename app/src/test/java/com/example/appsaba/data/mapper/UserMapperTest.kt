package com.example.appsaba.data.mapper

import com.example.appsaba.domain.model.User
import com.example.appsaba.data.local.entity.UserEntity
import org.junit.Assert.*
import org.junit.Test

class UserMapperTest {
    @Test
    fun roundtripPreservesLocalProfile() {
        val user = User(
            id = 8, name = "Operador", rut = "123", role = "admin",
            isActive = false, assignedParkingId = 42, cognitoSub = "sub-123"
        )
        assertEquals(user, user.toEntity().toDomain())
    }

    @Test
    fun nullableFieldsStayNull() {
        val entity = UserEntity(
            id = 1,
            name = "Sin RUT",
            rut = null,
            assignedParkingId = null,
            cognitoSub = null
        )
        val domain = entity.toDomain()
        assertNull(domain.rut)
        assertNull(domain.assignedParkingId)
        assertNull(domain.cognitoSub)
        assertEquals(entity, domain.toEntity())
    }
}
