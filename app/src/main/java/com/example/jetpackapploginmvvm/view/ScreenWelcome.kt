package com.example.jetpackapploginmvvm.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackapploginmvvm.viewmodel.LoginUiState

// COM PINTO LA PANTALLA?
@Composable
fun ScreenWelcome(
    state: LoginUiState,
    onLogoutClick: () -> Unit,
    onCloseClick: () -> Unit,
    msgWelcome: String
){
    Column (
        modifier = Modifier
            .fillMaxSize().padding(16.dp)
            .background(Color.Yellow),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = msgWelcome,
            color = Color.Yellow,
            fontSize = 24.sp,
            modifier = Modifier
                .background(Color.Black)
                .padding(16.dp)
                .fillMaxWidth()
        )

        Button(onClick = onLogoutClick) {Text("Canviar Usuari")}

        Button(onClick = onCloseClick) {Text("Tancar")}

    }
}


