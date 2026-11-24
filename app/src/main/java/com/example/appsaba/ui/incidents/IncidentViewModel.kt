package com.example.appsaba.ui.incidents

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.appsaba.domain.model.Incident
import com.example.appsaba.domain.repository.IncidentRepository
import com.example.appsaba.domain.repository.ParkingRepository
import com.example.appsaba.domain.repository.ServiceRepository
import com.example.appsaba.ui.common.CommonViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.UUID

/** Responsable SOLO de incidencias: estado, formularios y operaciones de tickets. */
@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class IncidentViewModel @Inject constructor(
    parkings: ParkingRepository,
    services: ServiceRepository,
    private val repository: IncidentRepository,
    @ApplicationContext private val context: Context
) : CommonViewModel() {
    private val selected = MutableStateFlow<Long?>(null)
    private var remoteToken: String? = null
    private val remoteIncidents = MutableStateFlow<List<Incident>>(emptyList())
    private val remoteBusy = MutableStateFlow(false)
    private val remoteFeedback = MutableStateFlow<String?>(null)
    private val reportVisible = MutableStateFlow(false)
    private val serviceFlow = selected.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else services.observeByParking(id)
    }
    private val incidentFlow = selected.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.observeByParking(id)
    }
    val uiState: StateFlow<IncidentUiState> = combine(
        parkings.observeAll(), parkings.observeRemote(), selected, serviceFlow, incidentFlow,
        reportVisible, status, remoteIncidents, remoteBusy, remoteFeedback
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val parkingList = (values[0] as List<com.example.appsaba.domain.model.Parking>) +
            (values[1] as List<com.example.appsaba.domain.model.Parking>)
        val parkingId = values[2] as Long?
        val serviceList = values[3] as List<com.example.appsaba.domain.model.Service>
        val ticketList = values[4] as List<Incident>
        val shown = values[5] as Boolean
        val action = values[6] as com.example.appsaba.ui.common.CommonUiState
        val serverParkingId = parkingList.firstOrNull { it.id == parkingId }?.remoteId
        // Un solo listado visible. Incidencias remotas no se insertan en Room sin
        // resolver primero las FK locales parkingId/serviceId y las evidencias.
        val visible = ticketList.filter { it.parkingId == parkingId } +
            (if (serverParkingId != null) (values[7] as List<Incident>).filter { it.parkingId == serverParkingId }
             else emptyList())
        IncidentUiState(
            parkings = parkingList,
            selectedParkingId = parkingId,
            services = serviceList.filter { it.parkingId == parkingId },
            incidents = visible,
            isReportSheetVisible = shown,
            busy = action.busy,
            feedback = action.feedback,
            remoteBusy = values[8] as Boolean,
            remoteFeedback = values[9] as String?,
            remoteAvailable = serverParkingId != null && remoteToken != null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), IncidentUiState())

    fun setRemoteToken(token: String?) {
        val normalized = token?.takeIf(String::isNotBlank)
        if (normalized == remoteToken) return
        remoteToken = normalized
        remoteIncidents.value = emptyList()
        remoteFeedback.value = null
        if (normalized != null) refreshRemote()
    }

    private fun remoteAction(operation: suspend (String) -> Unit) {
        val token = remoteToken ?: return
        if (remoteBusy.value) return
        remoteBusy.value = true
        remoteFeedback.value = null
        viewModelScope.launch {
            try {
                operation(token)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: HttpException) {
                if (remoteToken == token) remoteFeedback.value = "BFF: HTTP ${error.code()}; Room no fue modificado"
            } catch (error: Exception) {
                if (remoteToken == token) remoteFeedback.value = "Sin conexión al BFF: ${error.localizedMessage}"
            } finally {
                if (remoteToken == token) remoteBusy.value = false
            }
        }
    }

    private suspend fun reloadRemote(token: String) {
        val data = repository.listRemote(token)
        if (remoteToken == token) remoteIncidents.value = data
    }

    fun refreshRemote() = remoteAction { reloadRemote(it) }

    fun selectParking(id: Long?) { selected.value = id }
    fun openSheet() { reportVisible.value = true }
    fun closeSheet() { reportVisible.value = false }

    fun reportIncident(parkingId: Long?, serviceId: Long?, failure: String,
                       description: String, severity: String, reporter: String, evidenceUri: Uri? = null) {
        if (parkingId == null || serviceId == null || failure.isBlank() || description.isBlank() ||
            description.length > 240 || severity !in setOf("degradado", "fuera-servicio")) {
            reject("Selecciona equipo, falla y gravedad; describe el problema (máx. 240).")
            return
        }
        execute("Incidencia registrada localmente en SQLite") {
            val ticket = "SABA-${UUID.randomUUID().toString().take(8).uppercase()}"
            val evidence = evidenceUri?.let { persistEvidence(it) }
            repository.insert(Incident(ticketCode = ticket, parkingId = parkingId, serviceId = serviceId,
                failureType = failure.trim(), description = description.trim(), status = severity,
                reporterName = reporter.trim(), evidenceUri = evidence?.first,
                evidenceFileName = evidence?.second, evidenceMimeType = evidence?.third))
        }
    }
    /** URI externa temporal → archivo privado estable antes de insertar el ticket en SQLite. */
    private suspend fun persistEvidence(uri: Uri): Triple<String, String, String> = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val mime = requireNotNull(resolver.getType(uri)) { "No se pudo identificar el archivo." }
        require(mime.startsWith("image/") || mime.startsWith("video/")) { "Selecciona imagen o video." }
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "bin"
        val filename = "evidencia_${UUID.randomUUID()}.$ext"
        val dir = File(context.filesDir, "incident_evidence")
        require(dir.isDirectory || dir.mkdirs()) { "No se pudo crear carpeta de evidencias." }
        val file = File(dir, filename)
        try {
            resolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "No se pudo abrir la evidencia." }
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
        } catch (exception: Exception) {
            file.delete()
            throw exception
        }
        Triple(Uri.fromFile(file).toString(), filename, mime)
    }

    fun resolveIncident(incident: Incident, resolvedBy: String, solution: String) {
        if (resolvedBy.isBlank() || solution.isBlank() || solution.length > 300) {
            reject("Indica quién resolvió y qué hizo (máx. 300 caracteres).")
            return
        }
        if (incident.remoteId != null) {
            remoteAction { token ->
                repository.resolveRemote(token, incident, resolvedBy, solution)
                reloadRemote(token)
            }
        } else {
            execute("Resolución guardada localmente") {
                repository.update(incident.copy(isResolved = true, resolvedByName = resolvedBy.trim(),
                    resolutionDescription = solution.trim(), resolvedAt = System.currentTimeMillis()))
            }
        }
    }
    fun deleteIncident(incident: Incident) {
        if (incident.remoteId != null) {
            reject("El backend de Incidencias no dispone de DELETE")
            return
        }
        execute("Ticket eliminado") { repository.delete(incident) }
    }
}
