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

    testOptions {
        unitTests.isIncludeAndroidResources = true
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

        // Cognito debe utilizar la configuración REAL del User Pool. No usar placeholders
        // en BuildConfig: permiten generar un APK que fallará al autenticarse.
        val cognitoKeys = listOf(
            "COGNITO_ISSUER",
            "COGNITO_CLIENT_ID",
            "COGNITO_REDIRECT_URI",
            "COGNITO_LOGOUT_URI",
            "COGNITO_SCOPES"
        )
        cognitoKeys.forEach { key ->
            val value = envProps.getProperty(key)?.trim()?.takeIf { it.isNotEmpty() }
                ?: System.getenv(key)?.trim()?.takeIf { it.isNotEmpty() }
                ?: throw GradleException("Falta $key en .env o en el entorno de compilación")
            require(
                !value.contains("REEMPLAZAR", ignoreCase = true) &&
                        !value.contains("TU_USER_POOL", ignoreCase = true) &&
                        !value.contains("TU_CLIENT", ignoreCase = true)
            ) {
                "$key contiene un valor ficticio de Cognito"
            }
            val escaped = value.replace("\\", "\\\\").replace("\"", "\\\"")
            buildConfigField("String", key, "\"$escaped\"")
        }
    }

    // SABA release signing: private local credentials (not tracked by Git).
    val keystorePropertiesFile = file("keystore/upload-keystore.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
    }

    signingConfigs {
        create("upload") {
            storeType = "JKS"
            storeFile = file(keystoreProperties.getProperty("storeFile", "keystore/upload-keystore.jks"))
            storePassword = keystoreProperties.getProperty("storePassword", "")
            keyAlias = keystoreProperties.getProperty("keyAlias", "upload")
            keyPassword = keystoreProperties.getProperty("keyPassword", "")
        }
    }

    buildTypes {

        getByName("debug") {
            enableUnitTestCoverage = true
        }

        release {
            signingConfig = signingConfigs.getByName("upload")
            optimization {
                enable = false
                packageScope = setOf("androidx.**", "   kotlin.**", "kotlinx.**")
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
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
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
    implementation(platform(libs.androidx.compose.bom))

    ksp(libs.androidx.room.compiler)
    ksp("com.google.dagger:hilt-compiler:2.60.1")

    testImplementation("io.mockk:mockk:1.14.11")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    testImplementation(libs.junit)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation("org.robolectric:robolectric:4.16.1")
    testImplementation("androidx.test:core:1.6.1")
    testImplementation("androidx.test.ext:junit:1.2.1")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
