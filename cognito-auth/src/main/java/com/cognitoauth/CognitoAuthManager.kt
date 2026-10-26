package com.cognitoauth

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenResponse
import kotlin.coroutines.resume

/**
 * Fachada reutilizable sobre AppAuth-Android para Cognito (Authorization Code + PKCE).
 *
 * Responsabilidades:
 * - Descubrir endpoints vía `.well-known/openid-configuration` del issuer.
 * - Armar [AuthorizationRequest] con PKCE automático.
 * - Generar Intents de login/logout para lanzar con ActivityResultLauncher.
 * - Intercambiar el código por tokens.
 *
 * Lo que NO hace (lo hace la app): UI, navegación, persistencia de tokens,
 * ni registrar el callback en la consola de Cognito (ver docs/03_cognito.md).
 *
 * Uso mínimo:
 * ```
 * val manager = CognitoAuthManager(context, config)
 * val intent = manager.buildAuthIntent() // suspend: hace discovery
 * launcher.launch(intent)
 * // en onActivityResult:
 * manager.exchangeCode(data, onResult = { ... })
 * ```
 */
class CognitoAuthManager(
    context: Context,
    val config: CognitoConfig,
    appAuthConfig: AppAuthConfiguration = AppAuthConfiguration.Builder().build(),
) {
    init { config.validate() }

    private val authService = AuthorizationService(context.applicationContext, appAuthConfig)

    @Volatile
    private var serviceConfig: AuthorizationServiceConfiguration? = null

    /** Descubre (y cachea) la configuración OpenID del issuer. Suspende hasta responder. */
    suspend fun serviceConfiguration(): AuthorizationServiceConfiguration =
        serviceConfig ?: fetchServiceConfig().also { serviceConfig = it }

    private suspend fun fetchServiceConfig(): AuthorizationServiceConfiguration =
        suspendCancellableCoroutine { cont ->
            AuthorizationServiceConfiguration.fetchFromIssuer(
                config.issuerNetUri,
                AuthorizationServiceConfiguration.RetrieveConfigurationCallback { cfg, ex ->
                    if (cfg != null) cont.resume(cfg)
                    else cont.resumeWith(
                        Result.failure(ex ?: IllegalStateException("No se pudo descubrir Cognito"))
                    )
                },
            )
        }

    /** Arma el AuthorizationRequest con response_type=code y PKCE (AppAuth lo genera solo). */
    suspend fun buildAuthRequest(): AuthorizationRequest {
        val cfg = serviceConfiguration()
        return AuthorizationRequest.Builder(
            cfg,
            config.clientId,
            ResponseTypeValues.CODE,
            config.redirectNetUri,
        ).setScope(config.scopes).build()
    }

    /** Intent para abrir el Hosted UI (ej: `launcher.launch(manager.buildAuthIntent())`). */
    suspend fun buildAuthIntent(): Intent {
        val request = buildAuthRequest()
        return authService.getAuthorizationRequestIntent(request)
    }

    /**
     * Intercambia el código de autorización por tokens.
     * Llamar con el `Intent data` que llega al ActivityResultLauncher.
     * @param data Intent de respuesta (puede ser null si el usuario canceló).
     */
    fun exchangeCode(data: Intent?, onResult: (CognitoAuthResult) -> Unit) {
        val resp = data?.let { AuthorizationResponse.fromIntent(it) }
        val ex = data?.let { AuthorizationException.fromIntent(it) }

        if (resp == null) {
            if (ex != null) onResult(CognitoAuthResult.Failure(ex.message ?: "Autorización fallida", ex))
            else onResult(CognitoAuthResult.Cancelled)
            return
        }
        authService.performTokenRequest(
            resp.createTokenExchangeRequest(),
            AuthorizationService.TokenResponseCallback { token: TokenResponse?, tokenEx: AuthorizationException? ->
                if (token != null) {
                    onResult(
                        CognitoAuthResult.Success(
                            CognitoTokens(
                                idToken = token.idToken,
                                accessToken = token.accessToken,
                                refreshToken = token.refreshToken,
                                expiresIn = token.accessTokenExpirationTime?.let {
                                    (it - System.currentTimeMillis()) / 1000
                                },
                            )
                        )
                    )
                } else {
                    onResult(
                        CognitoAuthResult.Failure(
                            tokenEx?.message ?: "Intercambio de código fallido",
                            tokenEx,
                        )
                    )
                }
            },
        )
    }

    /**
     * Abre /logout de Cognito Hosted UI en el navegador.
     * El issuer es cognito-idp.<region>.amazonaws.com/<pool>, NO el dominio Hosted UI.
     * Por eso extraemos el dominio de la authorizationEndpoint descubierta con OIDC.
     *
     * Nota: este cierre elimina la cookie del navegador. No revoca refresh tokens.
     * Mantener el parámetro idToken por compatibilidad con llamadas anteriores.
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun buildLogoutIntent(idToken: String? = null): Intent {
        val authorizationEndpoint = serviceConfiguration().authorizationEndpoint
        require(authorizationEndpoint.scheme == "https") {
            "El endpoint de Cognito debe usar HTTPS"
        }
        val logoutUrl = authorizationEndpoint.buildUpon()
            .path("/logout")
            .clearQuery()
            .fragment(null)
            .appendQueryParameter("client_id", config.clientId)
            .appendQueryParameter("logout_uri", config.logoutUri)
            .build()

        return Intent(Intent.ACTION_VIEW, logoutUrl)
            .addCategory(Intent.CATEGORY_BROWSABLE)
    }

    fun dispose() = authService.dispose()
}
