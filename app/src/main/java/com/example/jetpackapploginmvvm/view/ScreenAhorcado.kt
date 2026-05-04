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
import androidx.compose.ui.graphics.ColorFilter
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

// Tenemos que meter este OptIn porque FlowRow todavía está marcado como experimental en la API,
// pero nos hace falta sí o sí para que el teclado salte de línea solo.
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScreenAhorcado(navController: NavController, viewModel: AhorcadoViewModel, username: String) {

    // Enganchamos la UI al Flow del ViewModel.
    // Cualquier cambio en los datos por debajo hará que Compose repinte mágicamente solo lo necesario.
    val uiState by viewModel.uiState.collectAsState()

    // Este bloque (con Unit) se ejecuta UNA SOLA VEZ nada más pisar esta pantalla.
    // Forzamos un reinicio para asegurarnos de que no nos comemos una partida a medias
    // si el usuario ha estado navegando de forma rara por la app.
    LaunchedEffect(Unit) {
        viewModel.reiniciarJuego()
    }

    // Esto es vital. Necesitamos saber cuándo el usuario minimiza la app (se va a WhatsApp, etc.).
    // DisposableEffect nos deja observar el ciclo de vida de la pantalla para apagar la música
    // y el acelerómetro en ON_PAUSE, y volver a encenderlos en ON_RESUME para no drenar batería a lo tonto.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.activarSensors()
                    viewModel.controlarMusicaFons(true)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.desactivarSensors()
                    viewModel.controlarMusicaFons(false)
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        // El onDispose es la "escoba". Se asegura de quitar el observer cuando esta pantalla muera del todo.
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Switch rapidito para saber qué imagen del muñeco toca pintar.
    // A menos vidas, más cerca de la muerte.
    val imagenAhorcado = when (uiState.intentosRestantes) {
        5 -> R.drawable.fail_1
        4 -> R.drawable.fail_2
        3 -> R.drawable.fail_3
        2 -> R.drawable.fail_4
        1 -> R.drawable.fail_5
        0 -> R.drawable.fail_6
        else -> R.drawable.fail_0
    }

    // Otro efecto secundario, pero este reacciona al flag 'navegarAGameOver'.
    // Cuando el ViewModel pone ese boolean a true (después de los 5 seg de delay), saltamos de pantalla.
    LaunchedEffect(uiState.navegarAGameOver) {
        if (uiState.navegarAGameOver) {
            val resultado = if (uiState.victoria) "¡Ganaste!" else "Perdiste"
            navController.navigate(AppScreens.GameOverScreen.createRoute(resultado, username)) {
                // Trucazo: Limpiamos el historial de navegación hacia atrás con popUpTo.
                // Así evitamos que si el usuario le da al botón físico de "Atrás" en la pantalla de GameOver,
                // vuelva a ver la partida terminada (lo cual quedaría fatal).
                popUpTo(AppScreens.AhorcadoScreen.route) { inclusive = true }
            }
        }
    }

    // El lienzo principal que nos pinta el fondo por defecto del tema (oscuro/claro)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // SpaceEvenly reparte el espacio que sobra por igual entre los elementos para que no quede todo apelotonado
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Text(
                text = "Intentos restantes: ${uiState.intentosRestantes}",
                fontSize = 24.sp,
                // Toque visual: Si le quedan menos de 3 vidas, pintamos el texto en rojo (error) para meter presión psicologica
                color = if (uiState.intentosRestantes < 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
            )

            Image(
                painter = painterResource(id = imagenAhorcado),
                contentDescription = "Estado del ahorcado",
                modifier = Modifier.size(200.dp).padding(16.dp)
            )

            // La magia de la palabra oculta. Mapeamos cada letra de la palabra secreta:
            // Si ya la ha pulsado, la enseñamos. Si no, metemos un '_' para ocultarla.
            // Al final lo juntamos todo separándolo por espacios para que se lea bien en pantalla.
            val palabraMostrada = uiState.palabraSecreta.map { letra ->
                if (uiState.letrasProbadas.contains(letra)) letra else '_'
            }.joinToString(" ")

            Text(
                text = palabraMostrada,
                fontSize = 48.sp,
                letterSpacing = 8.sp, // Separamos un poco más las letras para que parezca más un ahorcado clásico
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 60.sp, // Aquí estaba el error de la coma de antes ;)
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Usamos FlowRow en vez de Row o LazyVerticalGrid.
            // Esto escupe los botones uno detrás de otro y cuando no caben en la pantalla, bajan de línea solos.
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Un bucle directo del abecedario. Nos ahorramos tener que hacer una lista a mano con todas las letras.
                ('A'..'Z').map { letra ->
                    val yaProbada = uiState.letrasProbadas.contains(letra)

                    // Animamos el color del botón cuando lo pulsan. Tarda medio segundo en cambiar de gris a color primario.
                    // Queda muchísimo más pulido y "premium" que un cambio de color brusco.
                    val buttonColor by animateColorAsState(
                        targetValue = if (yaProbada) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                        animationSpec = tween(durationMillis = 500)
                    )

                    Button(
                        onClick = { viewModel.jugarLetra(letra) },
                        // Capamos el botón si ya le ha dado a esta letra o si el juego ha terminado.
                        // Así evitamos que la líe pulsando cosas mientras espera a que salte la pantalla de GameOver.
                        enabled = !yaProbada && !uiState.juegoTerminado,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = if (yaProbada) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(text = letra.toString())
                    }
                }
            }
        }
    }
}
