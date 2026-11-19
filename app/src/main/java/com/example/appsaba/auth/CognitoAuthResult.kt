package com.example.appsaba.auth

import com.example.appsaba.domain.model.Token

/** Resultado del Authorization Code + PKCE, sin acoplar la UI a AppAuth. */
sealed interface CognitoAuthResult {
    data class Success(val tokens: Token) : CognitoAuthResult
    data class Failure(val message: String, val cause: Throwable? = null) : CognitoAuthResult
    data object Cancelled : CognitoAuthResult
}
