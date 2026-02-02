package com.example.jetpackapploginmvvm.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackapploginmvvm.model.GameColor
import com.example.jetpackapploginmvvm.model.User
import com.example.jetpackapploginmvvm.model.UserRepository
import com.example.jetpackapploginmvvm.navigation.AppScreens
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// Aquests seran els estats que gestionaré a la sessió 2, pero comencen aixi
data class SimonUiState(
    val title: String = "Nivell 1",
    val isGameStarted: Boolean = false,
    // Grid size 2 x 2 es nivell facil
    val gridSizeX: Int = 2,
    val gridSizeY: Int = 2,
    val colors: List<GameColor> = emptyList(),
    var message: String = "Joc inactiu: Prem un color per començar"
)

class SimonViewmodel : ViewModel() {
    // estats de SimonUiState
    private val _uiState = mutableStateOf(SimonUiState())
    val uiState: State<SimonUiState> = _uiState

    init {
        // Inicialitzem el taulell amb els 4 colors bàsics (2x2)
        _uiState.value = _uiState.value.copy(
            colors = GameColor.values().take(_uiState.value.gridSizeX* _uiState.value.gridSizeY)
        )
    }

    /**
     * Acció quan l'usuari clica un color.
     * De moment només fem un print per consola per verificar la connexió.
     */
    fun onColorClick(color: GameColor) {
        if (_uiState.value.isGameStarted) {
            _uiState.value = _uiState.value.copy(
                message = "Has premut ${color.label}"
            )
            println("L'usuari ha polsat: ${color.label}")
            // Aquí anirà la lògica de comprovació a la Sessió 2
        } else {
            _uiState.value = _uiState.value.copy(
            isGameStarted = true,
            message = "Joc començat, pitja botons"
            )
        }
    }

    /**
     * Inicia el joc.
     */
    fun startGame() {
        _uiState.value = _uiState.value.copy(
            isGameStarted = true,
            message = "Repeteix la seqüència!"
        )
    }
    // SESSIÓ 1 No fer més lògica
    // Aquí anirà el control de la seqüència de colors.
}