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
    val victoria: Boolean = false
)

class AhorcadoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AhorcadoUiState())
    val uiState: StateFlow<AhorcadoUiState> = _uiState.asStateFlow()

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

        //Aqui le decimos a la aplicacion que se espere 5 segundos antes de cambiar.
        if (terminado) {
            viewModelScope.launch {
                delay(5000)

            }
        }
    }
}