package com.example.jetpackapploginmvvm.navigation

// Classe per centralitzar les possibles rutes i no tenir-les distribuides.
sealed class AppScreens (val route: String ){
    // Pantalla de Login: ruta simple
    data object Login : AppScreens("login_screen")

    // Pantalla de Welcome: ruta amb argument (el nom d'usuari)
    // welcome_screen/{username} és un patró per la llibreria NAVIGATION
    data object Welcome : AppScreens("welcome_screen/{username}"){
        fun createRoute(username: String) ="welcome_screen/${username}"

        //fun createRoute(username: String): String {
            // return "welcome_screen/$username"
        // }   // son expressions equivalents.
    }

    // Afegim la pantalla del Simon
    data object Simon : AppScreens( "simon_screen")
    // Aquesta pantalla no canvia cap dada, per tant no té paràmetres.

}