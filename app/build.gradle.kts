import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}


val envFile = rootProject.file(".env")
val envProps = Properties()
if (envFile.exists()) {
    envFile.inputStream().use { envProps.load(it) }
}
// Prioridad: .env > variable de entorno del sistema > fallback solo para compilar
val apiBaseUrl: String =
    envProps.getProperty("API_BASE_URL")
        ?: System.getenv("API_BASE_URL")
        ?: "https://172-233-15-71.ip.linodeusercontent.com/"




android {
    namespace = "com.example.appsaba"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.appsaba"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["appAuthRedirectScheme"] = "com.example.appsaba"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        // BFF independiente del API de login. 10.0.2.2 = host del emulador Android.
        // En Samsung real configurar BFF_API_BASE_URL con la IP LAN del PC.
        val bffApiBaseUrl = (envProps.getProperty("BFF_API_BASE_URL")
            ?: System.getenv("BFF_API_BASE_URL")
            ?: "http://10.0.2.2:8080/").trim()
        require(bffApiBaseUrl.startsWith("http://") || bffApiBaseUrl.startsWith("https://")) {
            "BFF_API_BASE_URL debe ser una URL HTTP(S)"
        }
        buildConfigField("String", "BFF_API_BASE_URL", "\"${bffApiBaseUrl.trimEnd('/')}/\"")

        val cognitoDefaults = mapOf(
            "COGNITO_ISSUER" to "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_REEMPLAZAR",
            "COGNITO_CLIENT_ID" to "REEMPLAZAR_CON_APP_CLIENT_ID",
            "COGNITO_REDIRECT_URI" to "com.example.appsaba://cognito/callback",
            "COGNITO_LOGOUT_URI" to "com.example.appsaba://cognito/logout",
            "COGNITO_SCOPES" to "openid email profile"
        )

        cognitoDefaults.forEach { (key, defaultValue) ->
            val value = envProps.getProperty(key)
                ?.ifBlank { null }
                ?: System.getenv(key)
                ?.ifBlank { null }
                ?: defaultValue

            buildConfigField("String", key, "\"$value\"")
        }
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    // Room: runtime + extensiones Kotlin; compilador separado via KSP
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.lifecycle.runtime.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("net.openid:appauth:0.11.1")
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended) // Visibility/VisibilityOff viven aquí, no en core
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation("com.google.dagger:hilt-android:2.60.1")
    ksp("com.google.dagger:hilt-compiler:2.60.1")
    implementation(platform(libs.androidx.compose.bom))

}
