package com.example.jetpackapploginmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackapploginmvvm.model.GameColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// S2 Estat del botò
// EL viewmodel no ha de treballar amb interfícies, només amb dades i per tant estats.
// defineixo l'estat d'un botó perquè la vista el pugui pintar.
data class ButtonState(
    val color: GameColor,
    val isLit: Boolean = false
)

// S2 canvio el llistat de colors a llistat d'estat dels butons, perquè conté totes les dades.
data class SimonUiState(
    // S4 TOT AIXÒ ARA CANVIA A PARTIR DEL NIVELL
    val title: String = "Nivell 1",
    val gridSizeX: Int = 2,
    val gridSizeY: Int = 2,
    val maxRounds: Int = 5,
    val speedMsWait: Long = 250,
    val speedMsGlow: Long = 600, // S04 tècnicament no el necessita la vista
    // però el declaro aqui per uniformar on deso i canvio paràmetres.

    val currentLevelIndex: Int = 0, // S4 Índex del llistat GAME_LEVELS
    val isGameStarted: Boolean = false,
    val isGamePaused: Boolean = false, // S4 cicle de vida.

    val buttons: List<ButtonState> = emptyList(),
    var message: String = "Joc inactiu: Prem un color per començar",

    // S3 Nous estats:
    val colorSequenceCPU: List<GameColor> = emptyList(), // S3 La combinació de colors a reproduir.
    val userTurnIndex: Int = 0, // Posició dins de la seqüència
    val isUserTurn: Boolean = false, // Si no es turn de l'usuari, no pot fer res

)


class SimonViewmodel : ViewModel() {
    // S03 estats de SimonUiState amb Flow perquè és d elògica (veure apunts)
    private val _uiState = MutableStateFlow(SimonUiState())
    val uiState = _uiState.asStateFlow()

    init {
        carregarNivell(0)
        _uiState.value = _uiState.value.copy(
            message = "Pitja Start per començar"
        )
    }
    
    // S04, ARA carregar el nivell actualitza moltes coses
    private fun carregarNivell(index: Int) {
        if (index >= GAME_LEVELS.size){
            // No hauria d'arribar mai aquí.
            _uiState.value = _uiState.value.copy(
                title = "JOC SUPERAT COMPLETAMENT",
                isGameStarted = false,
                isUserTurn = false,
                )
            return
        }
        val config = GAME_LEVELS[index]
        //val numBotons = config.rows * config.cols
        _uiState.value = _uiState.value.copy(
            isGameStarted = false, // Aturem el joc
            isUserTurn = false,
            currentLevelIndex = index,
            title = "Nivell ${config.levelNumber}",
            message = "ENHORABONA! \nHas superat el nivell ${index}",
            gridSizeX = config.cols,
            gridSizeY = config.rows,
            maxRounds = config.roundsToWin,
            speedMsGlow = config.speedMsGlow,
            speedMsWait = config.speedMsWait,
            colorSequenceCPU = emptyList(), // Reiniciem la seqüència
            userTurnIndex = 0,
            buttons = GameColor.getColorsForLevel(config.cols*config.rows).map { ButtonState(it) },
        )
    }

    //S03 ara quan l'usuari clica un color passen moltes coses diferents
    fun onColorClick(clickedColor: GameColor) {

        // S03, ara comença la CPU i l'usuari no ha de poder fer res.
        if (!_uiState.value.isGameStarted || !_uiState.value.isUserTurn){
             return
        }

        // S03 Encenc perque usuari sàquiga que ho he captat
         viewModelScope.launch {
             iluminarBoto(clickedColor, true)
             delay(150)
             iluminarBoto(clickedColor, false)

             // Faré servir molts cops _uiState.value, m'ho deso a una variable
             // però no la puc fer servir a la esquerra de l'igual.
             val uiStV = _uiState.value

             // S03 Comprovo si ha clicat la bona o no.
             val colorEsperat = uiStV.colorSequenceCPU[uiStV.userTurnIndex]
             if (clickedColor == colorEsperat){
                 val nouIndex = uiStV.userTurnIndex +1
                 if (nouIndex == uiStV.colorSequenceCPU.size){
                     // Final de la llista i de la ronda

                     // Comprovant si s'acaba el nivell
                     if (uiStV.colorSequenceCPU.size >= uiStV.maxRounds){
                         // NIVELL SUPERAT ! (tots els colors i maxim colors.
                         // ERA EL DARRER?
                         if(uiStV.currentLevelIndex >= GAME_LEVELS.size -1){
                             carregarNivell(0)
                             _uiState.value = _uiState.value.copy(
                                 message = "ENHORABONA! \nHas superat TOT EL JOC",
                             )
                         } else {
                             carregarNivell(uiStV.currentLevelIndex +1)
                         }
                     } else {
                         // El nivell segueix (tots els colors de la ronda, però no els maxims)
                         // Acabar Rondar i iniciar nova
                        _uiState.value = uiStV.copy(
                            message = "Ronda completada! Espera...",
                            isUserTurn = false
                        )
                        viewModelScope.launch {
                            delay(1000)
                            novaRonda()
                        }
                     }
                 } else {
                     // continua la ronda actual (falten colors)
                     _uiState.value = uiStV.copy(userTurnIndex = nouIndex)
                 }
             } else {
                 // Fallada
                 _uiState.value = uiStV.copy(
                     isGameStarted = false,
                     message = "Has fallat! Prem Start per reintentar."
                 )
             }
         }
    }
    
    // S03 iluminar el botó i apagar-lo
    private fun iluminarBoto(color: GameColor, doEncedre: Boolean){
        val newButtons = _uiState.value.buttons.map {
            // Aquest if ha de tornar a cada iteracio "it" un objecte del tipus 
            // que hi ha dins de la colecció buttons (per tant buttonState)
            if (it.color == color) it.copy(isLit = doEncedre) else it
            // si es el color que vull canviar, actualitzo l'estat
            // en cas contrari no faig res.
        }
        _uiState.value = _uiState.value.copy(
            buttons = newButtons
        )
    }
    
    // S03 Reproduir la sequència al torn de la CPU
    private suspend fun reproduirSequencia(sequence: List<GameColor>){
        delay(1000)
        for (color in sequence) {
            iluminarBoto(color, true)
            delay(_uiState.value.speedMsGlow) // S04 Ara això canvia !
            iluminarBoto(color,false)
            delay(_uiState.value.speedMsWait)
        }

        _uiState.value = _uiState.value.copy(
            isUserTurn = true,
            message = "El teu torn!"
        )
    }
    // S03 01 Ara he d'iniciar ronda
    fun startGame() {
        _uiState.value = _uiState.value.copy(
            isGameStarted = true,
            colorSequenceCPU = emptyList(),
            message = "Repeteix la seqüència!"
        )
        novaRonda()
    }
    
    // S03 02 La nova ronda
    private fun novaRonda() {
        // Random a una colecció directament agafa un element a l'atzar
        val randomColor = _uiState.value.buttons.random().color
        val newColorSequenceCPU = _uiState.value.colorSequenceCPU + randomColor
        
        _uiState.value = _uiState.value.copy(
            colorSequenceCPU = newColorSequenceCPU, // actualitzo la seqüència
            isUserTurn = false, // comença la CPU mostrant els colors
            message = "Memoritza la seqüència de colors...",
            userTurnIndex = 0 // L'usuari comença des del principi
        )
        
        // Llancem una corutina asincrona per reproduir seqüència
        viewModelScope.launch { 
            reproduirSequencia(newColorSequenceCPU)
        }
    }

}