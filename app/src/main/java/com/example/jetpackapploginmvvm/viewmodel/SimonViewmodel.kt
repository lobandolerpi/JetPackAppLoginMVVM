package com.example.jetpackapploginmvvm.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.jetpackapploginmvvm.model.GameColor

// S2 Estat del botò
// EL viewmodel no ha de treballar amb interfícies, només amb dades i per tant estats.
// defineixo l'estat d'un botó perquè la vista el pugui pintar.
data class ButtonState(
    val color: GameColor,
    val isLit: Boolean = false
)

// S2 canvio el llistat de colors a llistat d'estat dels butons, perquè conté totes les dades.
data class SimonUiState(
    val title: String = "Nivell 1",
    val isGameStarted: Boolean = false,
    // Grid size 2 x 2 es nivell facil
    val gridSizeX: Int = 2,
    val gridSizeY: Int = 2,
    val buttons: List<ButtonState> = emptyList(),
    var message: String = "Joc inactiu: Prem un color per començar"
)

class SimonViewmodel : ViewModel() {
    // estats de SimonUiState
    private val _uiState = mutableStateOf(SimonUiState())
    val uiState: State<SimonUiState> = _uiState

    init {
        // Inicialitzem el taulell amb els 4 colors bàsics (2x2)
        val GameColorReduit = GameColor.values().take(_uiState.value.gridSizeX* _uiState.value.gridSizeY)
        val initialButtons = GameColorReduit.map {
            color ->
            ButtonState(color = color)
        }
        // Exemple de map a una llista, sense lambda.
        val initialButtonsSenseLamba = mutableListOf<ButtonState>()
        for ( gameColorTMP in GameColorReduit) {
            val nouBotoAmbEstat = ButtonState(color = gameColorTMP)
            initialButtonsSenseLamba.add(nouBotoAmbEstat)
        }

       _uiState.value = _uiState.value.copy(  buttons = initialButtons )
    }

    /**
     * Acció quan l'usuari clica un color.
     * De moment només fem un print per consola per verificar la connexió.
     */
    fun onColorClick(clickedColor: GameColor) {
        //S2 El primer click el gestiono a part, perque ara faré més coses.
        if (!_uiState.value.isGameStarted){
             _uiState.value = _uiState.value.copy(
                isGameStarted = true,
                message = "Joc començat, pitja botons"
             )
        } else {

            //S2 Lògica d'il.luminació temporal
            // En el futur això ho canviarem per corrutines.
            val updatedButtons = _uiState.value.buttons.map { button ->
                if (button.color == clickedColor ) {
                    button.copy(isLit = true) // El boto pitjat s'il.lumina
                } else {
                    button.copy(isLit = false) // La resta s'apaguen si estan encesos.
                }
            }

            // S2, L'estat ara a més de canviar el text, ha d'actualitzar els botons.
            _uiState.value = _uiState.value.copy(
                buttons = updatedButtons,
                message = "Has premut ${clickedColor.label}"
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