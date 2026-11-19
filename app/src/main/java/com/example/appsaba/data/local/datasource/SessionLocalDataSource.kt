package com.example.appsaba.data.local.datasource

import android.content.Context
import com.example.appsaba.domain.model.Session
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Persistencia cifrada local. Nunca escribe JWTs en texto plano. */
class SessionLocalDataSource(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("saba_session_v2", Context.MODE_PRIVATE)
    private val alias = "saba_auth_session_aes_v1"


    private fun key(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(alias, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    fun save(session: Session) {
        val data = JSONObject().apply {
            put("id", session.idToken)
            put("access", session.accessToken ?: JSONObject.NULL)
            put("cognito", session.isCognito)
        }.toString().toByteArray(Charsets.UTF_8)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val encrypted = cipher.doFinal(data)
        val packed = cipher.iv + encrypted
        check(prefs.edit().putString("value", Base64.encodeToString(packed, Base64.NO_WRAP)).commit()) {
            "No se pudo guardar la sesión"
        }
    }

    fun load(): Session? {
        val encoded = prefs.getString("value", null) ?: return null
        return try {
            val packed = Base64.decode(encoded, Base64.NO_WRAP)
            require(packed.size > 12 + 16) { "Datos de sesión inválidos" }
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, packed.copyOfRange(0, 12)))
            val json = JSONObject(String(cipher.doFinal(packed.copyOfRange(12, packed.size)), Charsets.UTF_8))
            val session = Session(
                idToken = json.getString("id"),
                accessToken = if (json.isNull("access")) null else json.getString("access"),
                isCognito = json.getBoolean("cognito")
            )
            // No reutilizar una sesión Cognito cuando su ID Token ya ha expirado.
            if (session.isCognito && (tokenExpired(session.idToken) || session.accessToken.isNullOrBlank() || tokenExpired(session.accessToken))) {
                clear()
                null
            } else session
        } catch (_: Exception) {
            // Una clave de Android Keystore invalidada no debe provocar un crash de SABA.
            clear()
            null
        }
    }

    private fun tokenExpired(jwt: String): Boolean {
        return try {
            val body = jwt.split('.')[1]
            val payload = String(Base64.decode(body, Base64.URL_SAFE or Base64.NO_WRAP), Charsets.UTF_8)
            val exp = JSONObject(payload).optLong("exp", 0L)
            exp <= 0L || (exp * 1000L) <= (System.currentTimeMillis() + 30_000L)
        } catch (_: Exception) { true }
    }

    fun clear() { prefs.edit().remove("value").commit() }
}
