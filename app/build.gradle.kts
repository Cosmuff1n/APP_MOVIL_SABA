import java.util.Properties

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


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

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

        val cognitoVariables = listOf(
            "COGNITO_ISSUER",
            "COGNITO_CLIENT_ID",
            "COGNITO_REDIRECT_URI",
            "COGNITO_LOGOUT_URI",
            "COGNITO_SCOPES"
        )

        cognitoVariables.forEach { key ->
            val value = envProps.getProperty(key)
                ?: System.getenv(key)
                ?: ""

            require(value.isNotBlank()) {
                "Falta configurar $key en .env"
            }

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
    implementation(libs.androidx.room3.common)
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
    implementation(project(":cognito-auth"))
    implementation(platform(libs.androidx.compose.bom))

}