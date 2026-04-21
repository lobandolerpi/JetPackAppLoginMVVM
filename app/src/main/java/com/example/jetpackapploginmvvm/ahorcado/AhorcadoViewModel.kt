package com.example.jetpackapploginmvvm.ahorcado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AhorcadoUiState(
    val palabraSecreta: String = "JETPACK",
    val letrasProbadas: Set<Char> = emptySet(),
    val intentosRestantes: Int = 6,
    val juegoTerminado: Boolean = false,
    val victoria: Boolean = false,
    // AÑADIMOS ESTO: Un flag para saber cuándo han pasado los 5 segundos
    val navegarAGameOver: Boolean = false
)

class AhorcadoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AhorcadoUiState())
    val uiState: StateFlow<AhorcadoUiState> = _uiState.asStateFlow()

    // Mejora seleccionada: Diccionario Aleatorio [4]
    private val diccionario = listOf("JETPACK", "KOTLIN", "ANDROID", "COMPOSE", "CORRUTINA", "ROOM")

    fun jugarLetra(letra: Char) {
        val estadoActual = _uiState.value
        if (estadoActual.juegoTerminado || estadoActual.letrasProbadas.contains(letra)) return

        val nuevasLetras = estadoActual.letrasProbadas + letra
        val acierto = estadoActual.palabraSecreta.contains(letra)
        val nuevosIntentos = if (acierto) estadoActual.intentosRestantes else estadoActual.intentosRestantes - 1

        val todasLetrasAcertadas = estadoActual.palabraSecreta.all { nuevasLetras.contains(it) }
        val sinIntentos = nuevosIntentos <= 0
        val terminado = todasLetrasAcertadas || sinIntentos

        _uiState.value = estadoActual.copy(
            letrasProbadas = nuevasLetras,
            intentosRestantes = nuevosIntentos,
            juegoTerminado = terminado,
            victoria = todasLetrasAcertadas
        )

        // Aquí completamos tu corrutina
        if (terminado) {
            viewModelScope.launch {
                delay(5000) // Suspensión de 5 segundos sin congelar la app [2, 3]

                // Pasados los 5 segundos, actualizamos el estado para dar la orden de navegar
                _uiState.value = _uiState.value.copy(
                    navegarAGameOver = true
                )
            }
        }
    }
    fun reiniciarJuego() {
        val nuevaPalabra = diccionario.random()
        _uiState.value = AhorcadoUiState(
        palabraSecreta = nuevaPalabra,
        letrasProbadas = emptySet(),
        intentosRestantes = 6,
        juegoTerminado = false,
        victoria = false,
        navegarAGameOver = false // Importante resetear esto también
         )
    }
}