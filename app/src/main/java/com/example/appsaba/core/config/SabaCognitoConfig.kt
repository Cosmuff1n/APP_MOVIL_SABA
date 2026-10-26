package com.example.appsaba.config

import com.cognitoauth.CognitoConfig
import com.example.appsaba.BuildConfig

object SabaCognitoConfig {

    fun create(): CognitoConfig {
        return CognitoConfig(
            issuerUri = BuildConfig.COGNITO_ISSUER,
            clientId = BuildConfig.COGNITO_CLIENT_ID,
            redirectUri = BuildConfig.COGNITO_REDIRECT_URI,
            logoutUri = BuildConfig.COGNITO_LOGOUT_URI,
            scopes = BuildConfig.COGNITO_SCOPES
        )
    }
}