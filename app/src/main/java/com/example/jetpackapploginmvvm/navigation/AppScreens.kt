package com.example.jetpackapploginmvvm.navigation

// Aquí centralizamos todas las rutas de navegación de la app.
// Usar una 'sealed class' es la mejor forma de no liarla con 'typos' escribiendo
// los strings a mano cada vez que queremos cambiar de pantalla con el navController.
sealed class AppScreens (val route: String ){

    // Pantalla inicial, sin misterio. Ruta estática de toda la vida.
    data object Login : AppScreens("login_screen")

    // Aquí la cosa cambia. Necesitamos pasarle el nombre de usuario por la ruta.
    // El patrón "{username}" es lo que el NavHost de Compose lee para saber que ahí va una variable.
    data object Welcome : AppScreens("welcome_screen/{username}"){

        // Usamos esta funcioncita auxiliar cuando hacemos el navController.navigate(...).
        // Así nos construye el string final (ej. "welcome_screen/Paco") inyectando la variable,
        // en lugar de tener que concatenar strings nosotros a mano en cada botón.
        fun createRoute(username: String) ="welcome_screen/${username}"

        // fun createRoute(username: String): String {
        //     return "welcome_screen/$username"
        // }   // Son expresiones equivalentes, pero con el '=' queda más limpio.
    }

    // El minijuego de Simon. A esta pantalla entras a jugar y punto, no arrastra datos de momento.
    data object Simon : AppScreens( "simon_screen")

    // La pantalla principal de nuestro Ahorcado.
    // Nos arrastramos el username desde el Welcome (o de donde vengamos) para poder
    // mostrar de quién es la partida en la UI.
    data object AhorcadoScreen : AppScreens("ahorcado_screen/{username}") {
        fun createRoute(username: String) = "ahorcado_screen/$username"
    }

    // La pantalla de resultados al terminar el ahorcado.
    // Ojo aquí que le pasamos DOS parámetros seguidos en la ruta.
    // Necesitamos el resultado ("victoria" o "derrota") para saber qué imagen/sonido poner,
    // y el username para personalizar el mensajito.
    data object GameOverScreen : AppScreens("game_over_screen/{result}/{username}") {
        fun createRoute(result: String, username: String) = "game_over_screen/$result/$username"
    }
}