package com.example.appsaba

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.appsaba.auth.CognitoAuthManager
import com.example.appsaba.core.config.cognitoConfigFromBuild
import com.example.appsaba.ui.login.LoginScreen
import com.example.appsaba.ui.theme.AppSabaTheme
import com.example.appsaba.domain.model.Session
import com.example.appsaba.data.local.datasource.SessionLocalDataSource
import kotlinx.coroutines.launch

@dagger.hilt.android.AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppSabaTheme {
                // Nunca en SavedState: AndroidKeyStore cifra el estado persistido.
                val sessionStore = remember { SessionLocalDataSource(applicationContext) }
                val restored = remember { sessionStore.load() }
                var token: String? by remember { mutableStateOf(restored?.idToken) }
                var isCognitoSession by remember { mutableStateOf(restored?.isCognito == true) }
                var cognitoAccessToken: String? by remember { mutableStateOf(restored?.accessToken) }
                val scope = rememberCoroutineScope()

                val currentToken = token
                if (currentToken == null) {
                    LoginScreen(
                        onLoginSuccess = { t ->
                            runCatching { sessionStore.save(Session(t, null, false)) }
                            token = t
                            isCognitoSession = false
                            cognitoAccessToken = null
                        },
                        onCognitoLoginSuccess = { idToken, accessToken ->
                            runCatching { sessionStore.save(Session(idToken, accessToken, true)) }
                            token = idToken
                            cognitoAccessToken = accessToken
                            isCognitoSession = true
                        }
                    )
                } else {
                    AppNavigation(
                        accessToken = currentToken,
                        cognitoAccessToken = cognitoAccessToken,
                        onLogout = {
                            val closeCognito = isCognitoSession
                            sessionStore.clear()
                            // El token deja de estar accesible desde la UI de Saba.
                            token = null
                            isCognitoSession = false
                            cognitoAccessToken = null

                            if (closeCognito) {
                                scope.launch {
                                    val manager = CognitoAuthManager(
                                        applicationContext,
                                        cognitoConfigFromBuild()
                                    )
                                    try {
                                        // Abre /logout en el navegador; Cognito borrará
                                        // la cookie si esta solicitud se completa bien.
                                        startActivity(manager.buildLogoutIntent())
                                    } catch (error: Exception) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Sesión local cerrada. No se pudo abrir el cierre de Cognito.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } finally {
                                        manager.dispose()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}