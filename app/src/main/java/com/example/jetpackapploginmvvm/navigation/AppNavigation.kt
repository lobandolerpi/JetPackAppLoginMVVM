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
    
    //Funcions de suport per als botons de les pantalles
    fun ferLogout() = navController.navigate(AppScreens.Login.route, ::configurarPopUpLogin)
    fun anarASimon() = navController.navigate(AppScreens.Simon.route)
    fun tornarEnrere() = navController.popBackStack()

    // Funció de suport per processar la navegació que ve del ViewModel (Login)
    fun processarRutaViewModelLogin(route: String) {
        // Em pases un string i li dic al controlador on ha d'anar i om.
        navController.navigate(route, ::configurarPopUpLogin)
    }
    
    
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
                viewModel.navigationChannel.collect ( ::processarRutaViewModelLogin)
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
            arguments = listOf(navArgument("username", :: configurarArgUsername))
        ){
            // Alerta  -> !!!
            backStackEntry ->
            // backStackEntry és un paràmetre de composable,
            // en concret composable( route, arguments, (backStackEntry) -> {Lambda} )
            val username = backStackEntry.arguments?.getString("username") ?: "Desconegut"

            // Welcome no té estats, però si username com a paràmetre
            ScreenWelcome(
                msgWelcome = "Hola, $username",
                onLogoutClick = ::ferLogout,
                onCloseClick = onCloseApp,
                // NOU EVENT: Quan clickem jugar!
                onStartGame = ::anarASimon
            )
        }

        // NOVA RUTA 3 : La pantalla del Simon
        composable (route = AppScreens.Simon.route){
            ScreenSimon(
                onBackClick = ::tornarEnrere,
                //torna enrera 1 a l'historial de navegació.

                onCloseClick = onCloseApp
                // Hem definit onCloseApp de manera general.
            )
        }
    }
}