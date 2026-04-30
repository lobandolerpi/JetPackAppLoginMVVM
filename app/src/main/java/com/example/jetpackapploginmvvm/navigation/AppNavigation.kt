package com.example.jetpackapploginmvvm.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.jetpackapploginmvvm.viewmodel.WelcomeViewModel
import com.example.jetpackapploginmvvm.ahorcado.AhorcadoViewModel
import com.example.jetpackapploginmvvm.view.ScreenAhorcado
import com.example.jetpackapploginmvvm.view.ScreenGameOver
import com.example.jetpackapploginmvvm.model.AppDatabase
import com.example.jetpackapploginmvvm.model.UserDao

// Esta clase nos permite pasarle el DAO al ViewModel desde la Navegación
class WelcomeViewModelFactory(private val dao: UserDao) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WelcomeViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// FUNCIONS AUXILIARS FIRA DE LA UI

fun setInclusiveTrue(builder: PopUpToBuilder) {
    builder.inclusive = true
}

fun configurarPopUpLogin(builder: NavOptionsBuilder) {
    builder.popUpTo(AppScreens.Login.route, ::setInclusiveTrue)
}

fun configurarArgUsername(builder: androidx.navigation.NavArgumentBuilder) {
    builder.type = NavType.StringType
}

@Composable
fun AppNavigation(
    onCloseApp: () -> Unit
){
    val navController = rememberNavController()
    val ahorcadoViewModel: AhorcadoViewModel = viewModel()
    
    fun ferLogout() = navController.navigate(AppScreens.Login.route, ::configurarPopUpLogin)
    fun anarAAhorcado(username: String) = navController.navigate(AppScreens.AhorcadoScreen.createRoute(username))
    fun tornarEnrere() = navController.popBackStack()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Login.route
    ){
        composable( route= AppScreens.Login.route ){
            val loginViewModel: LoginViewModel = viewModel()
            val state by loginViewModel.uiState.collectAsState()
            
            val context = LocalContext.current
            val dao = AppDatabase.getDatabase(context).userDao()

            LaunchedEffect(state.loginSuccess) {
                if (state.loginSuccess) {
                    val username = state.username
                    loginViewModel.resetLoginSuccess()
                    navController.navigate(AppScreens.Welcome.createRoute(username)) {
                        popUpTo(AppScreens.Login.route) { inclusive = true }
                    }
                }
            }

            ScreenLogin(
                state = state,
                onUsernameChange = loginViewModel::onUsernameChange,
                onPasswordChange = loginViewModel::onPasswordChange,
                onRegisterClick = { loginViewModel.onRegisterClick(dao) },
                onLoginClick = { loginViewModel.onLoginClick(dao) },
                onCloseClick = onCloseApp
            )
        }

        composable(
            route = AppScreens.Welcome.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""

            // 1. Obtenemos la conexión a la base de datos
            val context = LocalContext.current
            val dao = AppDatabase.getDatabase(context).userDao()

            // 2. Creamos el ViewModel usando nuestra nueva Factory
            val welcomeVM: WelcomeViewModel = viewModel(
                factory = WelcomeViewModelFactory(dao)
            )

            // Llamamos a la pantalla
            ScreenWelcome(
                username = username,
                ranking = welcomeVM.rankingMundial,
                isLoading = welcomeVM.estaCarregant,
                mostrarDialogError = welcomeVM.mostrarDialogError,
                missatgeError = welcomeVM.textErrorDialog,
                onDismissDialog = { welcomeVM.amagarDialog() },
                onLogoutClick = ::ferLogout,
                onStartGame = { anarAAhorcado(username) }
            )
        }

        composable (route = AppScreens.Simon.route){
            ScreenSimon(
                onBackClick = ::tornarEnrere,
                onCloseClick = onCloseApp
            )
        }

        composable(
            route = AppScreens.AhorcadoScreen.route,
            arguments = listOf(navArgument("username", ::configurarArgUsername))
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            ScreenAhorcado(navController, ahorcadoViewModel, username)
        }

        composable(
            route = AppScreens.GameOverScreen.route,
            arguments = listOf(
                navArgument("result") { type = NavType.StringType },
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val resultado = backStackEntry.arguments?.getString("result") ?: "Fin"
            val username = backStackEntry.arguments?.getString("username") ?: ""

            ScreenGameOver(
                navController = navController,
                resultado = resultado,
                username = username,
                onRestart = { ahorcadoViewModel.reiniciarJuego() }
            )
        }
    }
}
