package com.example.jetpackapploginmvvm.ahorcado

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackapploginmvvm.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

// Guardamos todo el estado de la pantalla en un solo sitio.
// Así Compose solo tiene que observar este data class y repintar lo que cambie.
data class AhorcadoUiState(
    val palabraSecreta: String = "JETPACK", // La palabra que hay que adivinar.
    val letrasProbadas: Set<Char> = emptySet(), // Usamos un Set para no tener letras repetidas si el usuario pulsa a lo loco.
    val intentosRestantes: Int = 6, // Las vidas. 6 es el estándar del muñequito.
    val juegoTerminado: Boolean = false, // Flag para saber si bloqueamos el teclado.
    val victoria: Boolean = false, // ¿Ganó o perdió?
    val navegarAGameOver: Boolean = false // Trigger para saltar a la pantalla de resultados después del delay.
)

class AhorcadoViewModel(application: Application) : AndroidViewModel(application) {

    // Estado interno que modificamos nosotros y el estado público de solo lectura que expone el flujo hacia la UI
    private val _uiState = MutableStateFlow(AhorcadoUiState())
    val uiState: StateFlow<AhorcadoUiState> = _uiState.asStateFlow()

    // Un pequeño banco de palabras hardcodeado para ir tirando.
    private val diccionario = listOf("JETPACK", "KOTLIN", "ANDROID", "COMPOSE", "CORRUTINA", "ROOM")

    // --- VARIABLES DE AUDIO ---
    // MediaPlayer es ideal para música larga de fondo, SoundPool es mejor para efectos de sonido cortos (sin lag)
    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    // Guardamos las IDs que nos devuelve el SoundPool al cargar los archivos para luego llamarlos rápido
    private var sonidoAcierto: Int = 0
    private var sonidoError: Int = 0

    // --- VARIABLES DE SENSORES Y VIBRACIÓN ---
    // Pillamos el sensor del acelerómetro para el "easter egg" de agitar el móvil y pedir pista
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometre = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // La API de vibración cambió en Android 12 (S), así que toca hacer el if de versión de turno
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    // Guardamos cuándo fue la última vez que sacudió el móvil para evitar que pida 20 pistas por segundo
    private var ultimTempsSacsejada: Long = 0

    // El listener que está todo el rato escuchando los movimientos del móvil
    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                // Pillamos la fuerza de aceleración en los ejes X, Y, Z
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // Calculamos la fuerza total con la vieja confiable: el teorema de Pitágoras en 3D
                val aceleracion = sqrt((x * x + y * y + z * z).toDouble())

                // 15 es un buen valor a ojo. Más de 15 significa que le ha dado una buena sacudida
                if (aceleracion > 15) {
                    val tempsActual = System.currentTimeMillis()
                    // Ponemos un cooldown de 1 segundito entre sacudidas para que no se vuelva loco
                    if (tempsActual - ultimTempsSacsejada > 1000) {
                        ultimTempsSacsejada = tempsActual
                        pedirPista()
                    }
                }
            }
        }
    }

    init {
        // Nada más arrancar el ViewModel, preparamos toda la parafernalia de audios
        inicializarAudio()
    }

    // Configura el MediaPlayer para la música de fondo y carga los sonidos FX en memoria
    private fun inicializarAudio() {
        val context = getApplication<Application>()

        // Música de fondo en bucle y bajita (volumen al 30%) para que no moleste
        mediaPlayer = MediaPlayer.create(context, R.raw.bg_music)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(0.3f, 0.3f)

        // Boilerplate necesario para inicializar SoundPool hoy en día
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4) // Con 4 streams simultáneos vamos sobrados para este juego
            .setAudioAttributes(audioAttributes)
            .build()

        // Cargamos los efectos y nos guardamos sus IDs. El '1' del final es la prioridad.
        soundPool?.let { sp ->
            sonidoAcierto = sp.load(context, R.raw.acierto, 1)
            sonidoError = sp.load(context, R.raw.error, 1)
        }
    }

    // Método para mutear o desmutear la música, útil si metemos un botón de ajustes en la UI
    fun controlarMusicaFons(play: Boolean) {
        if (play && mediaPlayer?.isPlaying == false) mediaPlayer?.start()
        else if (!play && mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
    }

    // El core lógico del juego. Recibe la letra que pulsó el jugador y evalúa.
    fun jugarLetra(letra: Char) {
        val estadoActual = _uiState.value

        // Safety check: Si el juego ya acabó o el tío insiste en pulsar una letra ya probada, no hacemos nada.
        if (estadoActual.juegoTerminado || estadoActual.letrasProbadas.contains(letra)) return

        // Metemos la letra al saco de probadas
        val nuevasLetras = estadoActual.letrasProbadas + letra
        // Chequeamos si la letra existe en la palabra
        val acierto = estadoActual.palabraSecreta.contains(letra)

        // Damos algo de feedback visual/sonoro
        if (acierto) {
            soundPool?.play(sonidoAcierto, 1f, 1f, 1, 0, 1f)
        } else {
            soundPool?.play(sonidoError, 1f, 1f, 1, 0, 1f)
            vibrarError() // Le pegamos un chispazo al móvil si falla
        }

        // Restamos un intento solo si la cagó
        val nuevosIntentos = if (acierto) estadoActual.intentosRestantes else estadoActual.intentosRestantes - 1

        // ¿Están todas las letras de la palabra secreta dentro de nuestro set de letras probadas?
        val todasLetrasAcertadas = estadoActual.palabraSecreta.all { nuevasLetras.contains(it) }
        val sinIntentos = nuevosIntentos <= 0

        // Se acaba si acertó todas o si se quedó a cero vidas
        val terminado = todasLetrasAcertadas || sinIntentos

        // Actualizamos el estado de golpe para que Compose pinte los cambios
        _uiState.value = estadoActual.copy(
            letrasProbadas = nuevasLetras,
            intentosRestantes = nuevosIntentos,
            juegoTerminado = terminado,
            victoria = todasLetrasAcertadas
        )

        // Si se acabó la partida, le damos 5 segundos para que vea el resultado final antes de echarlo a otra pantalla
        if (terminado) {
            viewModelScope.launch {
                delay(5000)
                _uiState.value = _uiState.value.copy(
                    navegarAGameOver = true
                )
            }
        }
    }

    // Pone todo el state de fábrica para una partida nueva y pilla otra palabra
    fun reiniciarJuego() {
        val nuevaPalabra = diccionario.random()
        _uiState.value = AhorcadoUiState(
            palabraSecreta = nuevaPalabra,
            letrasProbadas = emptySet(),
            intentosRestantes = 6,
            juegoTerminado = false,
            victoria = false,
            navegarAGameOver = false
        )
    }

    // Hay que llamar a esto cuando la pantalla esté visible (onStart/onResume)
    fun activarSensors() {
        accelerometre?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    // IMPORTANTÍSIMO: Quitar el listener cuando no estemos en la app para no fundir la batería del usuario
    fun desactivarSensors() {
        sensorManager.unregisterListener(sensorEventListener)
    }

    // Un pequeño toque de vibración (300ms) para dar feedback negativo
    private fun vibrarError() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(300)
        }
    }

    // Saca una letra de las que le faltan por adivinar y la "pulsa" por el jugador
    private fun pedirPista() {
        val estadoActual = _uiState.value
        // Solo damos pista si el juego sigue en pie y le queda al menos 1 intento extra (por si la pista restara vida en un futuro)
        if (!estadoActual.juegoTerminado && estadoActual.intentosRestantes > 1) {
            // Filtramos la palabra secreta dejando solo las letras que aún NO están en letrasProbadas
            val letrasFaltantes = estadoActual.palabraSecreta.filter { letra ->
                !estadoActual.letrasProbadas.contains(letra)
            }
            // Pillamos una random y simulamos que el usuario la ha tocado
            if (letrasFaltantes.isNotEmpty()) {
                jugarLetra(letrasFaltantes.random())
            }
        }
    }

    // Se ejecuta justo antes de que el ViewModel muera.
    // Toca limpiar la casa y liberar memoria de cosas nativas como los audios y los sensores.
    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool?.release()
        soundPool = null
        desactivarSensors()
    }
}
