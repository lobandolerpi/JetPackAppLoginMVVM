package com.example.jetpackapploginmvvm.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.jetpackapploginmvvm.model.Mascota

class MascotaViewModel : ViewModel() {



//Esto es 60 segundos en total, es ajustable pero para las pruebas utilize 60 segundos
    private val TIEMPO_LIMITE = 1 * 60 * 1000L

   //Esto del mutable state es lo que explicaste de que de que si un valor cambia reedibujo la pantalla sin hacer peticion
    var mascota by mutableStateOf<Mascota?>(null)
        private set

    var estaComiendo by mutableStateOf(false)
        private set

    // Funcion de crear la mascota
    fun crearMascota(nombre: String) {
        mascota = Mascota(
            nom = nombre,
            ultimaVegadaQueVaMenjar = System.currentTimeMillis(), //Con esto miro cuando ha comido
            estaViva = true
        )
    }

    // Funcion para que coma
    fun darDeComer() {
        val mActual = mascota //Nos guardamos la mascota

        if (mActual != null) {
            mascota = Mascota(
                nom = mActual.nom, //mantenemos todos sus datos
                ultimaVegadaQueVaMenjar = System.currentTimeMillis(), //Y le indicamos que su tiempo de comida se reinicia
                estaViva = mActual.estaViva
            )

            estaComiendo = true

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                estaComiendo = false
            }, 2000) //Esto es para la "animacion de comer"
        }
    }

    // Funcion para comprobar si ha muerto
    fun comprobarSiSigueViva(): Boolean {
        val m = mascota ?: return false

        val tiempoTranscurrido = System.currentTimeMillis() - m.ultimaVegadaQueVaMenjar //Miramos cuanto ha sido el tiempo que lleva sin comer

        if (tiempoTranscurrido > TIEMPO_LIMITE) { //Si es mayor (es decir ha pasado el tiempo limite) pues muere
            mascota = m.copy(estaViva = false) //Y su estado pasa ha muerto
            return false
        }
        return true
    }

    fun resetearJuego() {
        mascota = null //hice una funcion para que si la mascota muere ya no puedas utilizarla (sea null)
    }
}