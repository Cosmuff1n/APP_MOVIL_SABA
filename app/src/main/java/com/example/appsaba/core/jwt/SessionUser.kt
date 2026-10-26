package com.example.appsaba.core.jwt

// data class = solo datos comparables/copiables (ver 01 Paso 3). Calca los claims del JWT.
// roles con default vacío para no crashear si el backend no lo manda.
data class SessionUser(
    val rut: String = "",
    val roles: List<String> = emptyList(),
    val iss: String = "",
    val sub: String = "",
    val iat: Long = 0,
    val exp: Long = 0
) {
    // Derivado: segundos restantes. Negativo = expirado.
    val secondsRemaining: Long get() = exp - (System.currentTimeMillis() / 1000)
    val isExpired: Boolean get() = secondsRemaining <= 0
}