package com.example.appsaba.ui.incidents

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class IncidentsViewModel : ViewModel() {

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    private val _showSheet = MutableStateFlow(false)
    val showSheet: StateFlow<Boolean> = _showSheet.asStateFlow()

    fun openSheet() { _showSheet.value = true }
    fun closeSheet() { _showSheet.value = false }

    fun crearTicket(ticket: Ticket) {
        _tickets.update { listOf(ticket.copy(id = UUID.randomUUID().toString())) + it }
        closeSheet()
    }
}