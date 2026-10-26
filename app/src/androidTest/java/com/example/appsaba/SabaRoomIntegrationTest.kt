package com.example.appsaba

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.appsaba.data.local.SabaDatabase
import com.example.appsaba.data.model.Incident
import com.example.appsaba.data.model.Parking
import com.example.appsaba.data.model.Service
import com.example.appsaba.data.repository.IncidentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Se ejecuta en un dispositivo Android; no modifica saba_database del usuario. */
@RunWith(AndroidJUnit4::class)
class SabaRoomIntegrationTest {

    @Test
    fun guardarConsultarActualizarYValidarRelacion() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, SabaDatabase::class.java).build()
        try {
            val parkingId = db.parkingDao().insert(
                Parking(name = "Parking test", address = "Dirección test", totalSpaces = 40, freeSpaces = 20)
            )
            val serviceId = db.serviceDao().insert(
                Service(parkingId = parkingId, name = "Barrera", type = "barriers")
            )
            val repo = IncidentRepository(db.incidentDao(), db.serviceDao())
            val incidentId = repo.insert(
                Incident(ticketCode = "TEST-001", parkingId = parkingId,
                    serviceId = serviceId, failureType = "Atasco", description = "No abre")
            )
            assertEquals(1, repo.observeByParking(parkingId).first().size)
            assertEquals(1, repo.observeActiveCount(parkingId).first())
            val ticket = requireNotNull(repo.getById(incidentId))
            repo.update(ticket.copy(isResolved = true, resolvedByName = "Técnico", resolvedAt = 123L))
            assertEquals(0, repo.observeActiveCount(parkingId).first())

            val anotherParkingId = db.parkingDao().insert(Parking(name = "Otro", address = "Otra dirección"))
            var rejected = false
            try {
                repo.insert(Incident(ticketCode = "TEST-002", parkingId = anotherParkingId,
                    serviceId = serviceId, failureType = "Falla", description = "Prueba"))
            } catch (_: IllegalArgumentException) {
                rejected = true
            }
            assertTrue("Debe bloquear equipos de otro estacionamiento", rejected)
        } finally {
            db.close()
        }
    }

    @Test
    fun laBasePersisteAlCerrarYReabrir() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbName = "saba_test_persistencia.db"
        context.deleteDatabase(dbName)
        try {
            val firstDb = Room.databaseBuilder(context, SabaDatabase::class.java, dbName).build()
            val id: Long
            try {
                id = firstDb.parkingDao().insert(Parking(name = "Persistente", address = "Prueba"))
            } finally {
                firstDb.close()
            }
            val reopened = Room.databaseBuilder(context, SabaDatabase::class.java, dbName).build()
            try {
                assertEquals("Persistente", reopened.parkingDao().getById(id)?.name)
            } finally {
                reopened.close()
            }
        } finally {
            context.deleteDatabase(dbName)
        }
    }
}
