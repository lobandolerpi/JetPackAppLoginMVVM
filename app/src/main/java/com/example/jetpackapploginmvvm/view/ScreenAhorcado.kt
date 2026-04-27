package com.example.jetpackapploginmvvm.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.jetpackapploginmvvm.R
import com.example.jetpackapploginmvvm.navigation.AppScreens
import com.example.jetpackapploginmvvm.ahorcado.AhorcadoViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScreenAhorcado(navController: NavController, viewModel: AhorcadoViewModel, username: String) {
    // Recolectamos el estado.
    val uiState by viewModel.uiState.collectAsState()

    // Inicialización del juego al entrar
    LaunchedEffect(Unit) {
        viewModel.reiniciarJuego()
    }

    // Gestión del ciclo de vida (Segundo plano / Primer plano)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Volvemos a la app: activamos sensores y música
                    viewModel.activarSensors()
                    viewModel.controlarMusicaFons(true)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    // App en segundo plano: pausamos sensores y música para ahorrar batería
                    viewModel.desactivarSensors()
                    viewModel.controlarMusicaFons(false)
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            // Al salir definitivamente de la pantalla, quitamos el observador
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val imagenAhorcado = when (uiState.intentosRestantes) {
        5 -> R.drawable.fail_1
        4 -> R.drawable.fail_2
        3 -> R.drawable.fail_3
        2 -> R.drawable.fail_4
        1 -> R.drawable.fail_5
        0 -> R.drawable.fail_6
        else -> R.drawable.fail_0
    }

    // Navegación automática cuando finalizamos el juego
    LaunchedEffect(uiState.navegarAGameOver) {
        if (uiState.navegarAGameOver) {
            val resultado = if (uiState.victoria) "¡Ganaste!" else "Perdiste"
            navController.navigate(AppScreens.GameOverScreen.createRoute(resultado, username)) {
                popUpTo(AppScreens.AhorcadoScreen.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = "Intentos restantes: ${uiState.intentosRestantes}", fontSize = 24.sp)

        Image(
            painter = painterResource(id = imagenAhorcado),
            contentDescription = "Estado del ahorcado",
            modifier = Modifier.size(200.dp).padding(16.dp)
        )
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
