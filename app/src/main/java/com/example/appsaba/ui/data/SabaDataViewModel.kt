package com.example.appsaba.ui.data

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appsaba.data.model.Incident
import com.example.appsaba.data.model.Parking
import com.example.appsaba.data.model.Service
import com.example.appsaba.data.model.User
import com.example.appsaba.data.repository.SabaRepositories
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Coordina el MVP de datos locales. La UI nunca accede directamente a los DAOs.
 * Room emite resultados mediante Flow y Compose los puede observar.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SabaDataViewModel(
    private val repositories: SabaRepositories,
    private val appContext: Context
) : ViewModel() {
    val parkings: StateFlow<List<Parking>> = repositories.parkingRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val users: StateFlow<List<User>> = repositories.userRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selectedParkingId = MutableStateFlow<Long?>(null)
    val selectedParkingId: StateFlow<Long?> = _selectedParkingId.asStateFlow()

    val services: StateFlow<List<Service>> = _selectedParkingId.flatMapLatest { parkingId ->
        if (parkingId == null) flowOf(emptyList())
        else repositories.serviceRepository.observeByParking(parkingId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val incidents: StateFlow<List<Incident>> = _selectedParkingId.flatMapLatest { parkingId ->
        if (parkingId == null) flowOf(emptyList())
        else repositories.incidentRepository.observeByParking(parkingId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback.asStateFlow()

    fun feedbackShown() { _feedback.value = null }
    fun selectParking(id: Long) { _selectedParkingId.value = id }

    private fun execute(success: String, task: suspend () -> Unit) {
        if (_busy.value) return
        _busy.value = true
        viewModelScope.launch {
            try {
                task()
                _feedback.value = success
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _feedback.value = "Operación no realizada: ${error.localizedMessage ?: "revisa los datos"}"
            } finally {
                _busy.value = false
            }
        }
    }

    fun saveParking(id: Long?, name: String, address: String, commune: String, total: String, free: String) {
        val totalSpaces = total.toIntOrNull()
        val freeSpaces = free.toIntOrNull()
        if (name.isBlank() || address.isBlank() || totalSpaces == null || freeSpaces == null ||
            totalSpaces < 0 || freeSpaces < 0 || freeSpaces > totalSpaces
        ) {
            _feedback.value = "Completa nombre, dirección y plazas válidas (libres <= totales)."
            return
        }
        execute("Estacionamiento guardado en SQLite") {
            if (id == null) repositories.parkingRepository.insert(
                Parking(name = name.trim(), address = address.trim(), commune = commune.trim(),
                    totalSpaces = totalSpaces, freeSpaces = freeSpaces)
            ) else {
                val previous = requireNotNull(repositories.parkingRepository.getById(id))
                repositories.parkingRepository.update(previous.copy(name = name.trim(),
                    address = address.trim(), commune = commune.trim(),
                    totalSpaces = totalSpaces, freeSpaces = freeSpaces))
            }
        }
    }

    fun toggleParking(parking: Parking) = execute("Estado del estacionamiento actualizado") {
        repositories.parkingRepository.update(parking.copy(isActive = !parking.isActive))
    }

    fun deleteParking(parking: Parking) = execute("Estacionamiento eliminado") {
        repositories.parkingRepository.delete(parking)
    }

    fun saveUser(id: Long?, name: String, rut: String, role: String, parkingId: Long?) {
        if (name.isBlank() || role.isBlank()) {
            _feedback.value = "Nombre y rol son obligatorios."
            return
        }
        execute("Perfil local guardado en SQLite") {
            if (id == null) repositories.userRepository.insert(
                User(name = name.trim(), rut = rut.trim().ifBlank { null },
                    role = role.trim(), assignedParkingId = parkingId)
            ) else {
                val previous = requireNotNull(repositories.userRepository.getById(id))
                repositories.userRepository.update(previous.copy(name = name.trim(),
                    rut = rut.trim().ifBlank { null }, role = role.trim(),
                    assignedParkingId = parkingId))
            }
        }
    }

    fun toggleUser(user: User) = execute("Perfil local actualizado") {
        repositories.userRepository.update(user.copy(isActive = !user.isActive))
    }

    fun deleteUser(user: User) = execute("Perfil local eliminado") {
        repositories.userRepository.delete(user)
    }

    fun saveService(id: Long?, parkingId: Long?, name: String, type: String, detail: String) {
        if (parkingId == null || name.isBlank() || type.isBlank()) {
            _feedback.value = "Selecciona un estacionamiento e indica nombre y tipo."
            return
        }
        execute("Equipo guardado en SQLite") {
            if (id == null) repositories.serviceRepository.insert(
                Service(parkingId = parkingId, name = name.trim(), type = type.trim(),
                    detail = detail.trim())
            ) else {
                val previous = requireNotNull(repositories.serviceRepository.getById(id))
                repositories.serviceRepository.update(previous.copy(name = name.trim(),
                    type = type.trim(), detail = detail.trim()))
            }
        }
    }

    fun setServiceStatus(service: Service, status: String) {
        if (status !in listOf("operativo", "degradado", "fuera-servicio")) return
        execute("Estado del equipo guardado") {
            repositories.serviceRepository.updateStatus(
                serviceId = service.id,
                status = status,
                problem = if (status == "operativo") null else service.problem
            )
        }
    }

    fun deleteService(service: Service) = execute("Equipo eliminado") {
        repositories.serviceRepository.delete(service)
    }

    fun reportIncident(parkingId: Long?, serviceId: Long?, failure: String,
                       description: String, severity: String, reporter: String,
                       evidenceUri: Uri? = null) {
        if (parkingId == null || serviceId == null || failure.isBlank() ||
            description.isBlank() || description.length > 240 ||
            severity !in listOf("degradado", "fuera-servicio")
        ) {
            _feedback.value = "Selecciona equipo, falla y gravedad; describe el problema (máx. 240)."
            return
        }
        execute("Incidencia registrada localmente en SQLite") {
            // UUID evita colisiones entre tickets creados rápidamente en el mismo teléfono.
            val ticket = "SABA-${UUID.randomUUID().toString().take(8).uppercase()}"
            // El selector de imágenes del formulario de Mantención devuelve un URI temporal.
            // Se copia a almacenamiento privado ANTES del INSERT, para que la evidencia
            // sobreviva al cierre de la app. No requiere permisos de galería adicionales.
            val evidence = evidenceUri?.let { persistEvidence(it) }
            repositories.incidentRepository.insert(
                Incident(ticketCode = ticket, parkingId = parkingId, serviceId = serviceId,
                    failureType = failure.trim(), description = description.trim(),
                    status = severity, reporterName = reporter.trim(),
                    evidenceUri = evidence?.first,
                    evidenceFileName = evidence?.second,
                    evidenceMimeType = evidence?.third)
            )
            // MVP local: la actualización del equipo es explícita en Equipos;
            // no afirmamos que el registro se haya enviado al servidor.
        }
    }

    /** Guarda la evidencia seleccionada en archivos privados de Saba. */
    private suspend fun persistEvidence(uri: Uri): Triple<String, String, String> =
        withContext(Dispatchers.IO) {
            val resolver = appContext.contentResolver
            val mime = resolver.getType(uri) ?: "application/octet-stream"
            require(mime.startsWith("image/") || mime.startsWith("video/")) {
                "Selecciona una fotografía o un video válido."
            }
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "bin"
            val filename = "evidencia_${UUID.randomUUID()}.$extension"
            val folder = File(appContext.filesDir, "incident_evidence")
            require(folder.isDirectory || folder.mkdirs()) { "No se pudo preparar el almacenamiento de evidencias." }
            val destination = File(folder, filename)
            try {
                resolver.openInputStream(uri).use { input ->
                    requireNotNull(input) { "No se pudo abrir el archivo seleccionado." }
                    FileOutputStream(destination).use { output -> input.copyTo(output) }
                }
            } catch (e: Exception) {
                destination.delete()
                throw e
            }
            Triple(Uri.fromFile(destination).toString(), filename, mime)
        }

    fun resolveIncident(incident: Incident, resolvedBy: String, solution: String) {
        if (resolvedBy.isBlank() || solution.isBlank() || solution.length > 300) {
            _feedback.value = "Indica quién resolvió y qué hizo (máx. 300 caracteres)."
            return
        }
        execute("Resolución guardada localmente") {
            repositories.incidentRepository.update(incident.copy(
                isResolved = true, resolvedByName = resolvedBy.trim(),
                resolutionDescription = solution.trim(), resolvedAt = System.currentTimeMillis()
            ))
        }
    }

    fun deleteIncident(incident: Incident) = execute("Ticket eliminado") {
        repositories.incidentRepository.delete(incident)
    }
}

class SabaDataViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext
    private val repositories = SabaRepositories(context.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SabaDataViewModel::class.java))
        return SabaDataViewModel(repositories, appContext) as T
    }
}
