package com.example.appsaba.data.mapper

import com.example.appsaba.data.local.entity.IncidentEntity
import com.example.appsaba.data.remote.dto.BffIncidentDto
import com.example.appsaba.domain.model.Incident
import org.junit.Assert.*
import org.junit.Test

class IncidentMapperTest {
    private fun remote() = BffIncidentDto(
        id = 22,
        ticketCode = "INC-22",
        parkingId = 4,
        serviceId = 8,
        failureType = "POS",
        description = "Falla",
        status = "degradado",
        reportedAt = 30
    )

    @Test
    fun roundtripRoomDomainKeepsEvidenceAndResolution() {
        val row = IncidentEntity(
            id = 3,
            ticketCode = "INC-3",
            parkingId = 4,
            serviceId = 8,
            failureType = "LPR",
            description = "Cámara",
            evidenceUri = "file://test",
            isResolved = true,
            resolvedByName = "Operador",
            resolutionDescription = "Reinicio",
            resolvedAt = 40
        )
        assertEquals(row, row.toDomain().toEntity())
    }

    @Test
    fun domainToEntityNeverCarriesRemotePrimaryKey() {
        val incident = Incident(
            ticketCode = "X", parkingId = 1, serviceId = 2,
            failureType = "POS", description = "No responde", remoteId = 99
        )
        assertEquals(0L, incident.toEntity().id)
    }

    @Test
    fun remoteDtoKeepsRemoteIdSeparateFromRoomId() {
        val incident = remote().toDomain()
        assertEquals(0L, incident.id)
        assertEquals(22L, incident.remoteId)
        assertEquals(4L, incident.parkingId)
    }

    @Test
    fun remoteMissingIdRejected() {
        assertTrue(runCatching {
            remote().copy(id = null).toDomain()
        }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun remoteMissingTicketRejected() {
        assertTrue(runCatching {
            remote().copy(ticketCode = null).toDomain()
        }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun remoteMissingParkingRejected() {
        assertTrue(runCatching {
            remote().copy(parkingId = null).toDomain()
        }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun remoteMissingServiceRejected() {
        assertTrue(runCatching {
            remote().copy(serviceId = null).toDomain()
        }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun remoteMissingFailureRejected() {
        assertTrue(runCatching {
            remote().copy(failureType = null).toDomain()
        }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun remoteMissingDescriptionRejected() {
        assertTrue(runCatching {
            remote().copy(description = null).toDomain()
        }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun remoteMissingStatusRejected() {
        assertTrue(runCatching {
            remote().copy(status = null).toDomain()
        }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun remoteOptionalFieldsUseDocumentedDefaults() {
        val incident =
            remote().copy(reportedAt = null, isResolved = null, reporterName = null).toDomain()
        assertEquals(0L, incident.reportedAt)
        assertFalse(incident.isResolved)
        assertEquals("", incident.reporterName)
    }

    @Test
    fun remoteResolvedFieldsArePreserved() {
        val incident = remote().copy(
            isResolved = true, resolvedByName = "Ana",
            resolutionDescription = "Arreglado", resolvedAt = 555L
        ).toDomain()
        assertTrue(incident.isResolved)
        assertEquals("Ana", incident.resolvedByName)
        assertEquals(555L, incident.resolvedAt)
    }
}
