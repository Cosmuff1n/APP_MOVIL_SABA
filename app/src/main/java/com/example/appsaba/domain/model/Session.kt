package com.example.appsaba.domain.model

/** Datos de sesion almacenados mediante Android Keystore; nunca en texto plano. */
data class Session(val idToken: String, val accessToken: String?, val isCognito: Boolean)
