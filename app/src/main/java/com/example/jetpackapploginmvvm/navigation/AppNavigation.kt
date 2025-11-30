package com.example.jetpackapploginmvvm.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackapploginmvvm.view.ScreenLogin
import com.example.jetpackapploginmvvm.view.ScreenWelcome
import com.example.jetpackapploginmvvm.viewmodel.LoginViewModel

@Composable
fun AppNavigation(
    onCloseApp: () -> Unit
){
    // El navegador és un tipus d'objecte de la llibreria navigation
    val navController = rememberNavController()

    // Això també és un objecte de la llibreria
    NavHost(
        navController = navController,
        startDestination = AppScreens.Login.route
    ){      // Aqui es defineixen les rutes
        // RUTA 1: Login
        composable( route= AppScreens.Login.route ){
            // instancia del viewModel de Login
            val viewModel: LoginViewModel = viewModel()

            val state by viewModel.uiState.collectAsState()
            // by es un operador que delega la responsabilitat de gestionar
            // com s'assigna l'objecte (en aquest cas els valors de l'estat)

            // Escoltem ordres de navegació
            LaunchedEffect(key1 = true) {
                // Les pot donar el viewModel (en aquest cas LoginViewModel
                viewModel.navigationChannel.collect {
                    // ens donarà la ruta de la següent pantalla
                    route ->
                    // La lambda ens diu que naveguem a la ruta
                    navController.navigate(route){
                        // Això evita que l'usuari pugui tornar al Login fent "enrere"
                        popUpTo(AppScreens.Login.route) {inclusive = true}
                    }
                }
            }

            // Finalment pintem la pantalla que ens han demanat si hem arribat aquí
            ScreenLogin(
                state = state,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onRegisterClick = viewModel::onRegisterClick,
                onLoginClick = viewModel::onLoginClick,
                onCloseClick = onCloseApp
            )
        }



        // RUTA 2 : WELCOME
        composable(
            route = AppScreens.Welcome.route,
            arguments = listOf(navArgument("username"){type = NavType.StringType})
        ){
            // Alerta  -> !!!
            backStackEntry ->
            // backStackEntry és un paràmetre de composable,
            // en concret composable( route, arguments, (backStackEntry) -> {Lambda} )
            val username = backStackEntry.arguments?.getString("username") ?: "Desconegut"

            // Welcome no té estats, però si username com a paràmetre
            ScreenWelcome(
                msgWelcome = "Hola, $username",
                onLogoutClick = {
                    navController.navigate(AppScreens.Login.route){
                        popUpTo(0)
                    }
                },
                onCloseClick = onCloseApp
            )
        }
    }
}