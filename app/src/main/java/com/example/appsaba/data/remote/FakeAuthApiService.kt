package com.example.appsaba.data.remote

import java.util.Base64
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * API SIMULADA únicamente para desarrollo (DEBUG).
 * Implementa el mismo contrato que Retrofit: AuthApiService.
 * No usa Internet ni autentica personas reales.
 */
class FakeAuthApiService : AuthApiService {

    override suspend fun login(body: LoginRequest): LoginResponse {
        delay(500) // Simula la espera de una petición a la API.

        // Se acepta RUT con o sin puntos, siempre conservando la clave JSON 'rut'.
        val rut = body.rut.trim().replace(".", "").uppercase(Locale.ROOT)
        if (rut != DEMO_RUT || body.password != DEMO_PASSWORD) {
            throw IllegalArgumentException("RUT o contraseña de prueba incorrectos")
        }

        val now = System.currentTimeMillis() / 1000L
        // Token local con formato header.payload.signature para probar la guía 02.
        // IMPORTANTE: no está firmado ni es una credencial de autenticación real.
        val header = """{"alg":"none","typ":"JWT","mock":true}"""
        val payload = """{"rut":"$DEMO_RUT","roles":["operador"],"iss":"saba-mock-debug","sub":"demo-1","iat":$now,"exp":${now + 3600}}"""
        val simulatedToken = "${encode(header)}.${encode(payload)}.MOCK_NOT_SIGNED"

        return LoginResponse(
            access_token = simulatedToken,
            token_type = "Bearer",
            expires_in = 3600,
            user = UserDto(id = 1, rut = DEMO_RUT)
        )
    }

    private fun encode(value: String): String =
        Base64.getUrlEncoder().withoutPadding()
            .encodeToString(value.toByteArray(Charsets.UTF_8))

    companion object {
        const val DEMO_RUT = "12345678-5"
        const val DEMO_PASSWORD = "Password123"
    }
}
