package com.example.appsaba.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {
    private val store = SessionStore(context)

    var idToken: String? by mutableStateOf(null)
        private set
    var accessToken: String? by mutableStateOf(null)
        private set
    var isCognito: Boolean by mutableStateOf(false)
        private set
    var lastAuthError: String? by mutableStateOf(null)
        private set

    init { restore() }

    fun restore() {
        val saved = store.load()
        idToken = saved?.idToken
        accessToken = saved?.accessToken
        isCognito = saved?.isCognito == true
    }

    fun localLogin(token: String) {
        store.save(SessionStore.SavedSession(token, null, false))
        idToken = token
        accessToken = null
        isCognito = false
        lastAuthError = null
    }

    fun cognitoLogin(idToken: String, accessToken: String) {
        store.save(SessionStore.SavedSession(idToken, accessToken, true))
        this.idToken = idToken
        this.accessToken = accessToken
        isCognito = true
        lastAuthError = null
    }

    fun authError(message: String?) { lastAuthError = message }
    fun clearAuthError() { lastAuthError = null }

    fun clear() {
        store.clear()
        idToken = null
        accessToken = null
        isCognito = false
        lastAuthError = null
    }
}
