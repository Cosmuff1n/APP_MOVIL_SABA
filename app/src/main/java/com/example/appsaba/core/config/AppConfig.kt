package com.example.appsaba.core.config

import com.example.appsaba.BuildConfig

object AppConfig {
    val apiBaseUrl: String = BuildConfig.API_BASE_URL.trim().let {
        if (it.endsWith("/")) it else "$it/"
    }
}