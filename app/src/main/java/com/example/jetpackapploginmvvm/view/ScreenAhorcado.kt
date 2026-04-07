package com.example.jetpackapploginmvvm.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jetpackapploginmvvm.navigation.AppScreens
import com.example.jetpackapploginmvvm.ahorcado.AhorcadoViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScreenAhorcado(navController: NavController, viewModel: AhorcadoViewModel) {
    // Recolectamos el estado.
    val uiState by viewModel.uiState.collectAsState()

    // Navegación automática cuando finalizamos el juego
    LaunchedEffect(uiState.juegoTerminado) {
        if (uiState.juegoTerminado) {
            // Mas pausas para dejar tiempo al usuario.
            kotlinx.coroutines.delay(1000)
            val resultado = if (uiState.victoria) "¡Ganaste!" else "Perdiste"
            navController.navigate(AppScreens.GameOverScreen.createRoute(resultado)) {
                popUpTo(AppScreens.AhorcadoScreen.route) { inclusive = true } // Evitamos que se pueda volver al juego.
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = "Intentos restantes: ${uiState.intentosRestantes}", fontSize = 24.sp)

        // Mostramos la palabra oculta
        val palabraMostrada = uiState.palabraSecreta.map { letra ->
            if (uiState.letrasProbadas.contains(letra)) letra else '_'
        }.joinToString(" ")

        Text(text = palabraMostrada, fontSize = 48.sp, letterSpacing = 8.sp)

        // Teclado usando un Map y lambdas.
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ('A'..'Z').map { letra ->
                val yaProbada = uiState.letrasProbadas.contains(letra)

                // Animamos cuando se presione el boton.
                val buttonColor by animateColorAsState(
                    targetValue = if (yaProbada) Color.Gray else MaterialTheme.colorScheme.primary,
                    animationSpec = tween(durationMillis = 500)
                )

                Button(
                    onClick = { viewModel.jugarLetra(letra) },
                    enabled = !yaProbada && !uiState.juegoTerminado,
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(text = letra.toString())
                }
            }
        }
    }
}

