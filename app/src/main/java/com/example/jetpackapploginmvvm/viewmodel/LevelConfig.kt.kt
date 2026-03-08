package com.example.jetpackapploginmvvm.viewmodel

data class LevelConfig(
    val levelNumber: Int, // Quin nivell estic
    val rows: Int,        // Files de botons Simon
    val cols: Int,        // Columnes de botons Simon
    val roundsToWin: Int, // Quantes rondes necessito per guanyar el nivell
    val speedMsGlow: Long,// La velocitat, ha de ser LONG pel mètode on ho faré servir
    val speedMsWait: Long, // La velocitat, ha de ser LONG pel mètode on ho faré servir
    val multiplicadorTemps: Double
)

// La nostra "Taula de Dificultat"
val GAME_LEVELS = listOf(
    LevelConfig(1, 2, 2, 3, 500L, 250L, 3.0),  // Nivell 1: 4 botons, 5 rondes
    LevelConfig(2, 3, 2, 3, 400L, 225L, 1.75), // Nivell 2: 6 botons, 10 rondes
    LevelConfig(3, 3, 2, 3, 300L, 200L, 1.35), // Nivell 3: 6 botons, 15 rondes
    LevelConfig(4, 3, 3, 3, 200L, 100L, 1.2)  // Nivell 4: 9 botons, 15 rondes
)
