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

data class AhorcadoUiState(
    val palabraSecreta: String = "JETPACK",
    val letrasProbadas: Set<Char> = emptySet(),
    val intentosRestantes: Int = 6,
    val juegoTerminado: Boolean = false,
    val victoria: Boolean = false,
    val navegarAGameOver: Boolean = false
)

class AhorcadoViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AhorcadoUiState())
    val uiState: StateFlow<AhorcadoUiState> = _uiState.asStateFlow()

    private val diccionario = listOf("JETPACK", "KOTLIN", "ANDROID", "COMPOSE", "CORRUTINA", "ROOM")

    // --- VARIABLES DE AUDIO ---
    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private var sonidoAcierto: Int = 0
    private var sonidoError: Int = 0

    // --- VARIABLES DE SENSORES Y VIBRACIÓN ---
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometre = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private var ultimTempsSacsejada: Long = 0

    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val aceleracion = sqrt((x * x + y * y + z * z).toDouble())

                if (aceleracion > 15) {
                    val tempsActual = System.currentTimeMillis()
                    if (tempsActual - ultimTempsSacsejada > 1000) {
                        ultimTempsSacsejada = tempsActual
                        pedirPista()
                    }
                }
            }
        }
    }

    init {
        inicializarAudio()
    }

    private fun inicializarAudio() {
        val context = getApplication<Application>()
        mediaPlayer = MediaPlayer.create(context, R.raw.bg_music)
        mediaPlayer?.isLooping = true
        mediaPlayer?.setVolume(0.3f, 0.3f)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.let { sp ->
            sonidoAcierto = sp.load(context, R.raw.acierto, 1)
            sonidoError = sp.load(context, R.raw.error, 1)
        }
    }

    fun controlarMusicaFons(play: Boolean) {
        if (play && mediaPlayer?.isPlaying == false) mediaPlayer?.start()
        else if (!play && mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
    }

    fun jugarLetra(letra: Char) {
        val estadoActual = _uiState.value
        if (estadoActual.juegoTerminado || estadoActual.letrasProbadas.contains(letra)) return

        val nuevasLetras = estadoActual.letrasProbadas + letra
        val acierto = estadoActual.palabraSecreta.contains(letra)

        if (acierto) {
            soundPool?.play(sonidoAcierto, 1f, 1f, 1, 0, 1f)
        } else {
            soundPool?.play(sonidoError, 1f, 1f, 1, 0, 1f)
            vibrarError()
        }

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

        if (terminado) {
            viewModelScope.launch {
                delay(5000)
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
            navegarAGameOver = false
        )
    }

    fun activarSensors() {
        accelerometre?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun desactivarSensors() {
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun vibrarError() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(300)
        }
    }

    private fun pedirPista() {
        val estadoActual = _uiState.value
        if (!estadoActual.juegoTerminado && estadoActual.intentosRestantes > 1) {
            val letrasFaltantes = estadoActual.palabraSecreta.filter { letra ->
                !estadoActual.letrasProbadas.contains(letra)
            }
            if (letrasFaltantes.isNotEmpty()) {
                jugarLetra(letrasFaltantes.random())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool?.release()
        soundPool = null
        desactivarSensors()
    }
}
