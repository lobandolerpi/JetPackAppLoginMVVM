package com.example.jetpackapploginmvvm.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jetpackapploginmvvm.navigation.AppScreens

@Composable
fun ScreenGameOver(navController: NavController, resultado: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = resultado, fontSize = 36.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            // Navegamos al Welcome presionando el boton.
            navController.navigate(AppScreens.Welcome.route) {
                popUpTo(0) // Limpiamos el historial
            }
        }) {
            Text("Volver al Inicio")
        }
    }
}