package com.example.jetpackapploginmvvm.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackapploginmvvm.viewmodel.AppScreens
import com.example.jetpackapploginmvvm.viewmodel.LoginEvent
import com.example.jetpackapploginmvvm.viewmodel.LoginViewModel

@Composable
fun ConnectorRoute(
    onCloseApp: () -> Unit,
    // Espero que em passis una funció sense arguments que no retorna res
    viewModel: LoginViewModel = viewModel()
    // Espero que em passis un LoginViewModel, però si no em passes res
    // faré servir viewModel() perque android crei un de nou.
){
    // compose ha de recollir l'estat dels camps de la App:
    val uiStateCompose = viewModel.uiState.collectAsState()
    // val uiState by viewModel.uiState.collectAsState()
        // by permet no posar .value

    LaunchedEffect( // LaunchedEffect : Això ho fas en una subrutina
        // per tal que no m'aturis la resta de la App.
        Unit
        // Unit mai canvia, per tant el 1r cop s'executarà això
        // i no farà res (es Unit).
    ) {
        viewModel.eventFlow.collect (
            // .eventFlow.collect  la subrutina espera fins a rebre un event
            // in cop rebi l'event l'analitzarà mitjançant ua lambda.

            // Això és una lambda
            { event  // Lo que sigui que em passin ho deso a event.
                ->
                if (event is LoginEvent.CloseApp) {
                    onCloseApp()
                    // si el que m'han passat es un event de CloseApp
                    // he d'executar la funció onCloseApp
                }
            }
        )
    }

    when (uiStateCompose.value.screenState){
        AppScreens.LOGIN -> {
            // Pinto la pantalla amb les dades adequades.
            ScreenLogin(
                state = uiStateCompose.value, // La vista vol només éls valors.
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onRegisterClick = viewModel::onRegisterClick,
                onLoginClick = viewModel::onLoginClick,
                onCloseClick = viewModel::onCloseClick
            )
        }
        AppScreens.WELCOME -> {
            ScreenWelcome(
                state = uiStateCompose.value,
                onLogoutClick = viewModel::onLogoutClick,
                onCloseClick = viewModel::onCloseClick,
                msgWelcome = "Benvingut/da " + uiStateCompose.value.username
            )
        }
        AppScreens.SETTINGS -> {
            ScreenWelcome(
                state = uiStateCompose.value,
                onLogoutClick = viewModel::onLogoutClick,
                onCloseClick = viewModel::onCloseClick,
                msgWelcome = "Funcionalitat en construcció"
            )
        }
        else -> {
            ScreenError(
                state = uiStateCompose.value,
                onCloseClick = viewModel::onCloseClick
            )
        }
    }
}



