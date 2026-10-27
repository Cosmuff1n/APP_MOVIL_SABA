package com.example.appsaba.ui.incidents

enum class EstadoEquipo { FUNCIONAL, DEGRADADO, FUERA_DE_SERVICIO}

data class Ticket(
    val id: String,
    val equipo: String,
    val tipoFalla: String,
    val estado: EstadoEquipo,
    val observaciones: String,
    val activo: Boolean = True
)