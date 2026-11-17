package com.example.appsaba

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.appsaba.data.local.SabaDatabase
import com.example.appsaba.data.local.datasource.IncidentLocalDataSourceImpl
import com.example.appsaba.data.local.datasource.ServiceLocalDataSourceImpl
import com.example.appsaba.data.local.entity.ParkingEntity
import com.example.appsaba.data.local.entity.ServiceEntity
import com.example.appsaba.data.repository.IncidentRepositoryImpl
import com.example.appsaba.data.repository.ServiceRepositoryImpl
import com.example.appsaba.domain.model.Incident
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Prueba de Room sobre base aislada (no altera datos reales de SABA). */
@RunWith(AndroidJUnit4::class)
class SabaRoomIntegrationTest {

    @Test
    fun guardarConsultarActualizarYValidarRelacion() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, SabaDatabase::class.java).build()
        try {
            val parkingId = db.parkingDao().insert(
                ParkingEntity(name = "Parking test", address = "Dirección test", totalSpaces = 40, freeSpaces = 20)
            )
            val serviceId = db.serviceDao().insert(
                ServiceEntity(parkingId = parkingId, name = "Barrera", type = "barriers")
            )
            val serviceRepo = ServiceRepositoryImpl(ServiceLocalDataSourceImpl(db.serviceDao()))
            val repo = IncidentRepositoryImpl(IncidentLocalDataSourceImpl(db.incidentDao()), serviceRepo)
            val incidentId = repo.insert(
                Incident(ticketCode = "TEST-001", parkingId = parkingId,
                    serviceId = serviceId, failureType = "Atasco", description = "No abre")
            )
            assertEquals(1, repo.observeByParking(parkingId).first().size)
            assertEquals(1, repo.observeActiveCount(parkingId).first())
            val ticket = requireNotNull(repo.getById(incidentId))
            repo.update(ticket.copy(isResolved = true, resolvedByName = "Técnico", resolvedAt = 123L))
            assertEquals(0, repo.observeActiveCount(parkingId).first())

            val anotherId = db.parkingDao().insert(ParkingEntity(name = "Otro", address = "Otra dirección"))
            var rejected = false
            try {
                repo.insert(Incident(ticketCode = "TEST-002", parkingId = anotherId,
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
            val first = Room.databaseBuilder(context, SabaDatabase::class.java, dbName).build()
            val id: Long
            try {
                id = first.parkingDao().insert(ParkingEntity(name = "Persistente", address = "Prueba"))
            } finally {
                first.close()
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
