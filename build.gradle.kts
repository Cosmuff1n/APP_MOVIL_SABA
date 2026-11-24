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
        // Servicio EC2 independiente del login (no modificar API_BASE_URL).
        val ec2ApiBaseUrl = envProps.getProperty("EC2_API_BASE_URL")
            ?: System.getenv("EC2_API_BASE_URL")
            ?: "http://34.203.14.129/"
        buildConfigField("String", "EC2_API_BASE_URL", "\"$ec2ApiBaseUrl\"")

        // No incorporar valores de ejemplo al APK: compilaría, pero Cognito fallaría al ingresar.
        // Los identificadores públicos se leen desde .env local o del entorno de compilación.
        val cognitoKeys = listOf(
            "COGNITO_ISSUER",
            "COGNITO_CLIENT_ID",
            "COGNITO_REDIRECT_URI",
            "COGNITO_LOGOUT_URI",
            "COGNITO_SCOPES"
        )
        val cognitoValues = cognitoKeys.associateWith { key ->
            val value = envProps.getProperty(key)?.trim()?.takeIf { it.isNotEmpty() }
                ?: System.getenv(key)?.trim()?.takeIf { it.isNotEmpty() }
                ?: throw GradleException(
                    "Falta $key. Configúralo en el archivo .env de la raíz (o en el entorno). " +
                        "Usa .env.example como plantilla; no subas .env a Git."
                )

            require(!value.contains("REEMPLAZAR", ignoreCase = true) &&
                    !value.contains("TU_USER_POOL", ignoreCase = true) &&
                    !value.contains("TU_CLIENT", ignoreCase = true)) {
                "$key contiene un valor de ejemplo. Usa la configuración real de Cognito."
            }
            value
        }

        require(cognitoValues.getValue("COGNITO_ISSUER").matches(
            Regex("https://cognito-idp\\.[a-z0-9-]+\\.amazonaws\\.com/[a-z0-9-]+_[A-Za-z0-9]+")
        )) {
            "COGNITO_ISSUER debe ser la URL del User Pool (cognito-idp), no el dominio /oauth2 de login."
        }
        require(cognitoValues.getValue("COGNITO_REDIRECT_URI") ==
                "com.example.appsaba://cognito/callback") {
            "COGNITO_REDIRECT_URI debe coincidir con el callback declarado en AndroidManifest.xml."
        }
        require(cognitoValues.getValue("COGNITO_LOGOUT_URI") ==
                "com.example.appsaba://cognito/logout") {
            "COGNITO_LOGOUT_URI debe coincidir con el logout declarado en AndroidManifest.xml."
        }
        require("openid" in cognitoValues.getValue("COGNITO_SCOPES").split(Regex("\\s+"))) {
            "COGNITO_SCOPES debe contener openid."
        }

        cognitoValues.forEach { (key, value) ->
            // Escapado para producir literales String válidos de Java en BuildConfig.
            val escaped = value.replace("\\", "\\\\").replace("\"", "\\\"")
            buildConfigField("String", key, "\"$escaped\"")
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

tasks.register("prepareKotlinBuildScriptModel") {
    // Placeholder task for IDE script model sync
}
