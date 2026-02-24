package com.example.jetpackapploginmvvm.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.PopUpToBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackapploginmvvm.view.ScreenLogin
import com.example.jetpackapploginmvvm.view.ScreenWelcome
import com.example.jetpackapploginmvvm.view.simon.ScreenSimon
import com.example.jetpackapploginmvvm.viewmodel.LoginViewModel
import com.example.jetpackapploginmvvm.view.ScreenMascotaCrear
import com.example.jetpackapploginmvvm.view.ScreenMascotaJoc
import com.example.jetpackapploginmvvm.view.ScreenMascotaMort
import com.example.jetpackapploginmvvm.viewmodel.MascotaViewModel


// FUNCIONS AUXILIARS FIRA DE LA UI

// A  FUNCIÓ PER FER LOGOUT (3 nivells de lambdes Esborrades)
// NIVELL 3 de lambdes:
// significat: quan tornis enrera fins a la pantalla X, esborra de memòria també la pantalla X
fun setInclusiveTrue(builder: PopUpToBuilder) {
    builder.inclusive = true
}
// NIVELL 2: La configuració del PopUp
fun configurarPopUpLogin(builder: NavOptionsBuilder) {
    // El constructor de pantalles saltarà a
    //   ruta   Login
    //   amb les instruccions de la funció (amb setInclusive, veure nivell 3.
    builder.popUpTo(AppScreens.Login.route, ::setInclusiveTrue)
}

// Configura el tipus d'argument 
fun configurarArgUsername(builder: androidx.navigation.NavArgumentBuilder) {
    builder.type = NavType.StringType
}

////////////////////////////////////////////////////////////////
// AQUI COMENCEM LA NAVEGACIÓ
@Composable
fun AppNavigation(
    onCloseApp: () -> Unit
){
    // El navegador és un tipus d'objecte de la llibreria navigation
    val navController = rememberNavController()
    val mascotaViewModel: MascotaViewModel = viewModel()
    //Funcions de suport per als botons de les pantalles
    fun ferLogout() = navController.navigate(AppScreens.Login.route, ::configurarPopUpLogin)
    fun anarASimon() = navController.navigate(AppScreens.Simon.route)
    fun tornarEnrere() = navController.popBackStack()

    fun anarACrearMascota() = navController.navigate(AppScreens.MascotaCrear.route)
    fun anarAlJocMascota() = navController.navigate(AppScreens.MascotaJoc.route)
    fun anarAMascotaMort() = navController.navigate(AppScreens.MascotaMort.route)

    fun processarRutaViewModelLogin(route: String) {
        navController.navigate(route, ::configurarPopUpLogin)
    }

    NavHost(navController = navController, startDestination = AppScreens.Login.route) {

        composable( route= AppScreens.Login.route ){
            val viewModel: LoginViewModel = viewModel()

            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(key1 = true) {
                viewModel.navigationChannel.collect ( ::processarRutaViewModelLogin)
            }

            ScreenLogin(
                state = state,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onRegisterClick = viewModel::onRegisterClick,
                onLoginClick = viewModel::onLoginClick,
                onCloseClick = onCloseApp
            )
        }



        composable(
            route = AppScreens.Welcome.route,
            arguments = listOf(navArgument("username", :: configurarArgUsername))
        ){ backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: "Desconegut"

            ScreenWelcome(
                msgWelcome = "Hola, $username",
                onLogoutClick = ::ferLogout,
                onCloseClick = onCloseApp,
                onStartGame = {
                    //AQUI compruebo si la mascota que tiene ese usuario ha muerto o no
                    //si esta muerta le obligo a ir a crear sino va a la mascota
                    if (mascotaViewModel.mascota != null && mascotaViewModel.mascota!!.estaViva) {
                        anarAlJocMascota()
                    } else {
                        anarACrearMascota()
                    }
                }
            )
        }

        composable (route = AppScreens.Simon.route){ //le quite todo al simon :(
            ScreenSimon(
                onBackClick = ::tornarEnrere,

                onCloseClick = onCloseApp
            )
        }


        composable(route = AppScreens.MascotaCrear.route) {
            ScreenMascotaCrear(
                viewModel = mascotaViewModel,
                onMascotaCreada = ::anarAlJocMascota,
                onBackClick = ::tornarEnrere
            )
        }

        composable(route = AppScreens.MascotaJoc.route) {
            ScreenMascotaJoc(
                viewModel = mascotaViewModel,
                onMascotaMorta = ::anarAMascotaMort,

                onBackClick = {
                    //Esto lo tuve que buscar para que si le dabas se fuese al welcome no a crear demonop
                    navController.popBackStack(AppScreens.Welcome.route, inclusive = false)
                }
            )
        }
        composable(route = AppScreens.MascotaMort.route) {
            ScreenMascotaMort(
                viewModel = mascotaViewModel,
                onReiniciarJoc = {
                    //aqui igual es para que vaya a crear la mascota si reinicia
                    navController.navigate(AppScreens.MascotaCrear.route) {
                        popUpTo(AppScreens.MascotaCrear.route) { inclusive = true }
                    }
                }
            )
        }
    }
    }
