package com.example.jetpackapploginmvvm.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackapploginmvvm.viewmodel.LoginEvent
import com.example.jetpackapploginmvvm.viewmodel.LoginUiState
import com.example.jetpackapploginmvvm.viewmodel.LoginViewModel

@Composable
fun LoginRoute(
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

    // Pinto la pantalla amb les dades adequades.
    LoginScreen(
        state = uiStateCompose.value, // La vista vol només éls valors.
        onUserChangeName = viewModel::onUserChangeName,
        onPasswordChange = viewModel::onPasswordChangeName,
        onRegisterClick = viewModel::onRegisterClick,
        onLoginClick = viewModel::onLoginClick,
        onCloseClick = viewModel::onCloseClick
    )
}

// COM PINTO LA PANTALLA?
@Composable
private fun LoginScreen(
    state: LoginUiState,
    onUserChangeName: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onCloseClick: () -> Unit
){
    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.message,
            modifier = Modifier
                .background(Color.Cyan)
                .padding(16.dp)
                .fillMaxWidth()
        )

        OutlinedTextField( value = state.username, onValueChange = onUserChangeName, label = { Text("User") })
        OutlinedTextField( value = state.password, onValueChange = onPasswordChange, label = { Text("Pass") })

        Row {
            Button(onClick = onRegisterClick) { Text("Crear usuari") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onLoginClick) { Text("Entrar") }
        }

        Button(onClick = onCloseClick) {Text("Tancar")}

        Text(
            text = state.errorMsg,
            modifier = Modifier
                .background(Color(255,200,200))
                .padding(16.dp)
                .fillMaxWidth()
        )
    }
}


