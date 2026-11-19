package com.example.appsaba.core.jwt

import android.util.Base64
import com.example.appsaba.domain.model.SessionUser
import org.json.JSONObject

/**
 * Lee claims para presentacion; decodificar NO valida la firma del JWT.
 * El backend es responsable de validar firma, emisor, audience y expiracion.
 */
object JwtDecoder {
    fun decode(accessToken: String): Result<SessionUser> = runCatching {
        val pieces = accessToken.split('.')
        require(pieces.size == 3) { "Token sin formato JWT" }
        val body = String(Base64.decode(pieces[1], Base64.URL_SAFE or Base64.NO_WRAP), Charsets.UTF_8)
        val json = JSONObject(body)
        val claims = json.optJSONArray("roles") ?: json.optJSONArray("cognito:groups")
        val roles = if (claims == null) emptyList() else (0 until claims.length()).mapNotNull {
            claims.optString(it).takeIf(String::isNotBlank)
        }
        SessionUser(
            rut = json.optString("rut").ifBlank { json.optString("email") },
            roles = roles,
            iss = json.optString("iss"),
            sub = json.optString("sub"),
            iat = json.optLong("iat", 0L),
            exp = json.optLong("exp", 0L)
        )
    }
}
