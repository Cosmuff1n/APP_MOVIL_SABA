package com.example.appsaba

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.cognitoauth.CognitoAuthManager
import com.example.appsaba.config.SabaCognitoConfig
import com.example.appsaba.ui.navigation.SabaMainScaffold
import com.example.appsaba.ui.login.LoginScreen
import com.example.appsaba.ui.theme.AppSabaTheme
import kotlinx.coroutines.launch

@dagger.hilt.android.AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppSabaTheme {
                // Durante esta etapa no conservamos tokens JWT en el estado guardado
                // de Android. Si la app se recrea, se pide iniciar sesión otra vez.
                var token: String? by remember { mutableStateOf(null) }
                var isCognitoSession by remember { mutableStateOf(false) }
                var cognitoAccessToken: String? by remember { mutableStateOf(null) }
                val scope = rememberCoroutineScope()

                val currentToken = token
                if (currentToken == null) {
                    LoginScreen(
                        onLoginSuccess = { t ->
                            token = t
                            isCognitoSession = false
                            cognitoAccessToken = null
                        },
                        onCognitoLoginSuccess = { idToken, accessToken ->
                            token = idToken
                            cognitoAccessToken = accessToken
                            isCognitoSession = true
                        }
                    )
                } else {
                    SabaMainScaffold(
                        accessToken = currentToken,
                        cognitoAccessToken = cognitoAccessToken,
                        onLogout = {
                            val closeCognito = isCognitoSession
                            // El token deja de estar accesible desde la UI de Saba.
                            token = null
                            isCognitoSession = false
                            cognitoAccessToken = null

                            if (closeCognito) {
                                scope.launch {
                                    val manager = CognitoAuthManager(
                                        applicationContext,
                                        SabaCognitoConfig.create()
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
