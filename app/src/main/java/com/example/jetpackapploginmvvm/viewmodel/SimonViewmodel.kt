package com.example.jetpackapploginmvvm.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.wifi.ScanResult
import android.net.wifi.WifiAvailableChannel
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackapploginmvvm.R
import com.example.jetpackapploginmvvm.model.GameColor
import kotlinx.coroutines.Job
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
    val timerValueInitial: Int = 20,
    val timerValueRemaining: Int = -1,


    val buttons: List<ButtonState> = emptyList(),
    var message: String = "Joc inactiu: Prem un color per començar",

    // S3 Nous estats:
    val colorSequenceCPU: List<GameColor> = emptyList(), // S3 La combinació de colors a reproduir.
    val userTurnIndex: Int = 0, // Posició dins de la seqüència
    val isUserTurn: Boolean = false, // Si no es turn de l'usuari, no pot fer res

)

// S05A Canviar // class SimonViewmodel : ViewModel() // per :
class SimonViewmodel (application: Application): AndroidViewModel(application) {
    // S03 estats de SimonUiState amb Flow perquè és d elògica (veure apunts)
    private val _uiState = MutableStateFlow(SimonUiState())
    val uiState = _uiState.asStateFlow()
    private var timerJob: Job? = null

    // S05 variables per desar sensor i manager
    private var sensorManagerApp: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var ultimTempsSacsejada: Long = 0

    // S06: Variables de control multimèdia
    private var mediaPlayer: MediaPlayer? = null // Sons llargs, es carreguen des de disc.
    private var soundPool: SoundPool? = null // Sons curts, es carreguen a RAM, s'executen sense latència.
    private val soundMap = mutableMapOf<Int, Int>() // Relaciona el GameColor.id amb el So

    init {
        carregarNivell(0)
        _uiState.value = _uiState.value.copy(
            message = "Pitja Start per començar"
        )
        // S05A Afegim crida al nou mètode de crida
        llistarSensorsDisponibles()
        llistarServeisLocalitzacio()
        llistarServeisAudio()
        inicialitzarAudio()
    }

    //S06 Prepara els fitxers i canals d'audio per que estiguin llestos per la app.
    private fun inicialitzarAudio() {
        val context = getApplication<Application>()
        // Utilitzem el Context de l'aplicació per accedir als recursos del sistema
        // i del paquet (fitxers, bases de dades, carpetes 'res/raw', etc.).

        // A. Música de fons amb MediaPlayer
        mediaPlayer = MediaPlayer.create(
            context,
            R.raw.music_background // ATENCIÓ
            // Importa  import com.example.jetpackapploginmvvm.R
            // El de la teva app, no cap altre predefinit.
            // el fitxer està a RAW del directori de la teva APP!
        )
        mediaPlayer?.isLooping = true // Volem que soni en bucle


        // B. Efectes de so amb SoundPool
        // Configurem les característiques de l'àudio (com s'ha d'escoltar)
        val audioAttributes = AudioAttributes.Builder()
            // Indiquem el propòsit: USAGE_GAME prioritza la baixa latència (ideal per a jocs)
            .setUsage(AudioAttributes.USAGE_GAME)
            // Tipus de contingut: SONIFICATION s'usa per a efectes de so curts (clics, explosions, etc.)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // Construïm la instància de SoundPool
        soundPool = SoundPool.Builder()
            .setMaxStreams(4) // Permetem fins a 4 sons trepitjant-se (polifonia)
            // Assignem els atributs definits anteriorment per optimitzar el rendiment de l'àudio
            .setAudioAttributes(audioAttributes)
            .build()


        // C. Carreguem els 9 sons i els mapegem a la ID del GameColor
        // Fem servir let/run o simplement load(). Si un so falta, es desa com 0.
        soundPool?.let { sp ->
            soundMap[1] = sp.load(context, R.raw.s01, 1)
            soundMap[2] = sp.load(context, R.raw.s02, 1)
            soundMap[3] = sp.load(context, R.raw.s03, 1)
            soundMap[4] = sp.load(context, R.raw.s04, 1)
            soundMap[5] = sp.load(context, R.raw.s05, 1)
            soundMap[6] = sp.load(context, R.raw.s06, 1)
            soundMap[7] = sp.load(context, R.raw.s07, 1)
            soundMap[8] = sp.load(context, R.raw.s08, 1)
            soundMap[9] = sp.load(context, R.raw.s09, 1)
        }
    }

    // S06 Funció per disparar un so concret
    private fun reproduirSoColor(colorId: Int) {
        val soundId = soundMap[colorId]
        if (soundId != null && soundId != 0) {
            // paràmetres: id, volumEsq, volumDret, prioritat, loop(0=no), velocitat(1f=normal)
            soundPool?.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }


    // S06 Funció per gestionar la música
    private fun controlarMusicaFons(play: Boolean) {
        if (play) {
            if (mediaPlayer?.isPlaying == false) mediaPlayer?.start()
        } else {
            if (mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
        }
    }



    //S05A Exploració de sensors sense lambdes
    private fun llistarSensorsDisponibles() {
        // a) Obtenim el gestor de sensors fent servir el context segur de l'Application
        val sensorManager = getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            // a.1  getApplication<Application>() -> Des del nostre ViewModel que ara rep l'app, agafem el contenidor global de tota la nostra app.
            // a.2  .getSystemService(...) -> Utilitzem el registre d'aquesta app per demanar un SERVEI al Sistema Operatiu.
            // a.3: Context.SENSOR_SERVICE -> Quin servei volem? El departament de Sensors.
            // a.4: as SensorManager -> Com que el sistema ens retorna un 'servei genèric', fem un cast per dir-li a Kotlin: 'Tracta això com el que és, un gestor de sensors'.

        // b) Demanem al gestor una llista amb TOTS (TYPE_ALL) els sensors del dispositiu
        val llistaSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        // c) Imprimim la informació al Logcat per analitzar-la a classe
        Log.d("SENSORS_SIMON", "--- INICI LLISTAT DE SENSORS ---")
        Log.d("SENSORS_SIMON", "S'han trobat ${llistaSensors.size} sensors al dispositiu.")
        for (sensor in llistaSensors) {
            Log.d("SENSORS_SIMON", "Sensor: ${sensor.name} | Tipus: ${sensor.stringType} | Fabricant: ${sensor.vendor}")
        }
        Log.d("SENSORS_SIMON", "--- FI LLISTAT DE SENSORS ---")
    }

    private fun llistarServeisLocalitzacio() {
        val locatManager = getApplication<Application>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val llistaLocation: List<String> = locatManager.getProviders(true)
        Log.d("LOCALITZADORS_SIMON", "--- INICI LLISTAT DE PROVEIDORS ---")
        Log.d("LOCALITZADORS_SIMON", "S'han trobat ${llistaLocation.size} sensors al dispositiu.")
        for (provider in llistaLocation) {
            Log.d("LOCALITZADORS_SIMON", "Proveidor: ${provider} ")
        }
        Log.d("LOCALITZADORS_SIMON", "--- FI LLISTAT DE PROVEIDORS DE LOCALITZACIÓ ---")
    }


    private fun llistarServeisAudio() {
        val audioManager = getApplication<Application>().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // EXERCICI LLISTAR EL VOLUM ACTUAL DE LES TRUCADES, ALARMES i MÚSICA
        //   val audioManager = getApplication<Application>().getSystemService( ???? ) as ?__?
        //   val arrayAudio: ?**?  =  audioManager.getStreamVolume(?__?.STREAM_?--?)
        val volumLlamadas: Int = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        val volumAlarma: Int = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        val volumMusica: Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        Log.d("VOLUMEN", "Volumen Llamadas ${volumLlamadas}")
        Log.d("VOLUMEN", "Volumen Alarma ${volumAlarma}")
        Log.d("VOLUMEN", "Volumen Musica ${volumMusica}")

    }



    // S05 Creem l'escoltador SENSE lambdes, implementant la interfície directament
    private val sensorEventListener = object : android.hardware.SensorEventListener {

        // Aquesta funció salta quan la precisió del sensor canvia (no la fem servir ara)
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // El compilador força a definirla.
            // Pero la nostra app no necessita precissió, una sacsejada és una sacsejada sempre.
        }

        // Aquesta funció salta cada vegada que l'acceleròmetre detecta un canvi
        override fun onSensorChanged(event: android.hardware.SensorEvent?) {
            if (event != null) {
                // Obtenim l'acceleració en els 3 eixos (X, Y, Z)
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // Dividim per la gravetat terrestre per saber quantes "G" de força estem aplicant
                val gX = x / SensorManager.GRAVITY_EARTH
                val gY = y / SensorManager.GRAVITY_EARTH
                val gZ = z / SensorManager.GRAVITY_EARTH

                // Calculem la força G total (magnitud del vector)
                val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()
                if (gForce > 0.5f) {
                    Log.d("SENSOR:", "x: ${x}, y: ${y} , z: ${z}, g: ${gForce}")
                }

                // Si la força G és superior a 1.5 (una sacsejada forta)
                if (gForce > 1.2f) {
                    val tempsActual = System.currentTimeMillis()
                    Log.d("SENSOR:", "x: ${x}, y: ${y} , z: ${z}, g: ${gForce}")
                    Log.d("SENSOR:", "tempsActual: ${tempsActual}, ultimTempsSacsejada: ${ultimTempsSacsejada}")
                    Log.d("SENSOR:", "diff temps: ${tempsActual - ultimTempsSacsejada}")
                    Log.d("SENSOR:", "Game started?: ${_uiState.value.isGameStarted}")
                    // Només fem cas si han passat almenys 2 segons (2000 ms) des de l'última sacsejada
                    if (tempsActual - ultimTempsSacsejada > 2000) {
                        //if (_uiState.value.isGameStarted) {
                            ultimTempsSacsejada = tempsActual
                            Log.d("SENSORS_SIMON", "SACSEJADA DETECTADA! Força G: $gForce. Reiniciant nivell...")
                            carregarNivell(_uiState.value.currentLevelIndex)
                            _uiState.value = _uiState.value.copy(
                                timerValueRemaining = tempsMax(),
                            )
                            novaRondaMsgSacsejada()
                        //}
                    }
                }
            }
        }
    }
    // S05 Funciño per encendre el sensor
    fun activarSensor() {
        if (sensorManagerApp == null) {
            sensorManagerApp = getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManagerApp?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }

        // Registrem el nostre escoltador. SENSOR_DELAY_UI és una velocitat pensada per jocs/interfícies
        accelerometer?.let {
            sensorManagerApp?.registerListener(
                sensorEventListener, // qui escolta
                it, // a qui s'escolta
                SensorManager.SENSOR_DELAY_UI // cada quant s'escolta.
            )

            Log.d("SENSORS_SIMON", "Sensor activat: Escoltant moviments.")
        }
    }
    // S05 Funciño per apagar el sensor i no gastar bateria
    fun desactivarSensor() {
        // Parem d'escoltar per estalviar bateria
        sensorManagerApp?.unregisterListener(sensorEventListener)
        Log.d("SENSORS_SIMON", "Sensor desactivat: Estalviant bateria.")
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
            pararTimer()
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
        // S06 Apago la música pq apago el joc.
        controlarMusicaFons(false)

        pararTimer()
    }

    // S04, per tornar del joc pausar pel que sigui
    fun anarAPausa() {
        pararTimer()
        carregarNivell(_uiState.value.currentLevelIndex)
        _uiState.value = _uiState.value.copy(
            message = "El nivell ${_uiState.value.currentLevelIndex+1} s'ha interromput \n Prem Start per reiniciar-lo"
        )
    }

    fun tempsMax() : Int{
        val config = GAME_LEVELS[_uiState.value.currentLevelIndex]
        val numClicks = (uiState.value.maxRounds)*(uiState.value.maxRounds +1)/2
        return ( numClicks.toDouble() * config.multiplicadorTemps ).toInt()
    }
    fun pararTimer() {
        timerJob?.cancel()
    }

    fun iniciarTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            // Comença des del valor que hi hagi a l'estat (si eren 5s, comença a 5s)
            for (i in _uiState.value.timerValueRemaining downTo 0) {
                _uiState.value = _uiState.value.copy(timerValueRemaining = i)
                delay(1000)
            }
            // Si arriba a 0, han perdut per temps: reiniciem el nivell i el temps total
            _uiState.value = _uiState.value.copy(timerValueRemaining = 0)
            anarAPausa()
            _uiState.value = _uiState.value.copy(message = "TEMPS EXHAURIT!\nTorna a intentar-ho.")
        }
    }
    
    
    //S03 ara quan l'usuari clica un color passen moltes coses diferents
    fun onColorClick(clickedColor: GameColor) {

        // S03, ara comença la CPU i l'usuari no ha de poder fer res.
        if (!_uiState.value.isGameStarted || !_uiState.value.isUserTurn){
            pararTimer()
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
                                 timerValueRemaining = tempsMax(),
                             )
                         } else {
                             carregarNivell(uiStV.currentLevelIndex +1)
                             _uiState.value = _uiState.value.copy(
                                 timerValueRemaining = tempsMax(),
                             )
                         }
                     } else {
                         // El nivell segueix (tots els colors de la ronda, però no els maxims)
                         // Acabar Rondar i iniciar nova
                        _uiState.value = uiStV.copy(
                            message = "Ronda completada! Espera...",
                            isUserTurn = false

                        )
                        pararTimer()
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
                 // S06 Apago la música pq apago el joc.
                 controlarMusicaFons(false)
             }
         }
    }

    // S06 Actualitzo la funció afegint els sons aquí.
    // AIXÍ M'ASSEGURO QUE L'ESTAT D'IL.LUMINAR I EL DEL SO
    // ES CANVIEN SIMULTÂNEAMENT I VAN COORDINATS
    // S03 iluminar el botó i apagar-lo
    private fun iluminarBoto(color: GameColor, doEncedre: Boolean){
        val newButtons = _uiState.value.buttons.map {
            // Aquest if ha de tornar a cada iteracio "it" un objecte del tipus 
            // que hi ha dins de la colecció buttons (per tant buttonState)
            if (it.color == color) {
                if (doEncedre){ // S06 Només si estic encenent.
                    // reprodueixo el so del color corresponent.
                    reproduirSoColor(color.id)
                }
                // S06 ATENCIÓ AQUEST IF HA DE RETORNAR EL BOTÓ
                // AIXÍ QUE AQUESTA LÍNIA HA DE SER LA ÚLTIMA.
                it.copy(isLit = doEncedre)
            } else it
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
        iniciarTimer()
    }


    // S03 01 Ara he d'iniciar ronda
    fun startGame() {
        _uiState.value = _uiState.value.copy(
            isGameStarted = true,
            colorSequenceCPU = emptyList(),
            message = "Repeteix la seqüència!"
        )
        // S06 Encenc la música pq encenc el joc.
        controlarMusicaFons(true)

        // Si el timerValue ja és 0 (perquè han perdut), el tornem a posar al màxim del nivell
        if (_uiState.value.timerValueRemaining <= 0) {
            _uiState.value = _uiState.value.copy(timerValueRemaining = tempsMax() )
        }

        novaRonda()
        iniciarTimer() // El timer reprèn des del valor actual de _uiState.value.timerValue

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
            userTurnIndex = 0, // L'usuari comença des del principi
            isGameStarted = true,
        )
        // S06 Encenc la música pq encenc el joc.
        controlarMusicaFons(true)
        pararTimer()
        
        // Llancem una corutina asincrona per reproduir seqüència
        viewModelScope.launch { 
            reproduirSequencia(newColorSequenceCPU)
        }
    }

    private fun novaRondaMsgSacsejada() {
        // Random a una colecció directament agafa un element a l'atzar
        val randomColor = _uiState.value.buttons.random().color
        val newColorSequenceCPU = _uiState.value.colorSequenceCPU + randomColor

        _uiState.value = _uiState.value.copy(
            colorSequenceCPU = newColorSequenceCPU, // actualitzo la seqüència
            isUserTurn = false, // comença la CPU mostrant els colors
            message = " Sacsejada: Reiniciant lvl ${_uiState.value.currentLevelIndex + 1}\n Memoritza la seqüència de colors...",
            userTurnIndex = 0, // L'usuari comença des del principi
            isGameStarted = true,
        )
        // S06 Encenc la música pq encenc el joc.
        controlarMusicaFons(true)
        pararTimer()

        // Llancem una corutina asincrona per reproduir seqüència
        viewModelScope.launch {
            reproduirSequencia(newColorSequenceCPU)
        }
    }

    //S06 Sobreescribim com s'esborra aquest viewmodel
    // per tal que alliberem els recursos del so
    // i altres pantalles o aplicacions els puguin fer servir
    // i a més no gastem RAM o altres recursos innecessàriament.
    override fun onCleared() {
        // Aturem i alliberem la memòria a l'S.O.
        // SEMPRE, SEMPRE, SEMPRE que activem coses que gasten recursos
        // cal alliberar-les després
        mediaPlayer?.release()
        mediaPlayer = null

        soundPool?.release()
        soundPool = null
    }

}