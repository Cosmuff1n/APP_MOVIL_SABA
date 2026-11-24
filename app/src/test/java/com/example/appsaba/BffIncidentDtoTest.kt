package com.example.appsaba

import com.example.appsaba.data.mapper.toDomain
import com.example.appsaba.data.remote.dto.BffIncidentDto
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class BffIncidentDtoTest {
    @Test fun createPayloadNeverSendsServerGeneratedIds() {
        val body = BffIncidentDto(ticketCode = "SABA-TEST01", parkingId = 3L,
            serviceId = 5L, failureType = "Barrera", description = "No responde",
            status = "degradado")
        val json = Gson().toJson(body)
        assertFalse(json.contains("\"id\""))
        assertFalse(json.contains("\"reportedAt\""))
        assertTrue(json.contains("\"parkingId\":3"))
    }

    @Test fun remoteIdRemainsIndependentFromRoom() {
        val incoming = BffIncidentDto(id = 20L, ticketCode = "INC-20",
            parkingId = 3L, serviceId = 5L,
            failureType = "POS", description = "Error", status = "fuera-servicio",
            reportedAt = 100L, isResolved = false)
        val incident = incoming.toDomain()
        assertEquals(0L, incident.id)
        assertEquals(20L, incident.remoteId)
        assertEquals(3L, incident.parkingId)
        assertFalse(incident.isResolved)
    }

    @Test(expected = IllegalArgumentException::class)
    fun remoteResponseMustContainId() {
        BffIncidentDto(ticketCode = "INC-0", parkingId = 3L, serviceId = 5L).toDomain()
    }
}
