package com.example.appsaba.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.appsaba.MainActivity

/** Recibe SOLO com.example.appsaba://cognito/logout y vuelve a Saba. */
class CognitoLogoutRedirectActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        )
        finish()
    }
}
