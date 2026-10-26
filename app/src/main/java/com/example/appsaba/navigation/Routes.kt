package com.example.appsaba.navigation

// Un solo lugar con los nombres de las rutas evita errores de tipeo.
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val INCIDENTS = "incidents"
    const val GUIDES = "guides"
    const val MAINTENANCE = "maintenance"

    const val DETAIL = "detail"                    // ruta base
    const val DETAIL_PATTERN = "detail/{serviceId}" // ruta con argumento

    // Arma la ruta final, por ejemplo "detail/atm2"
    fun detail(serviceId: String) = "$DETAIL/$serviceId"
}
