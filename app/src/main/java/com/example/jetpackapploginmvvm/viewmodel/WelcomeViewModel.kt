package com.example.jetpackapploginmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.jetpackapploginmvvm.model.UserDao
import com.example.jetpackapploginmvvm.model.api.RemoteUser
import com.example.jetpackapploginmvvm.model.api.RankingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeViewModel(private val dao: UserDao) : ViewModel() {

    // Estados para la Interfaz
    var rankingMundial by mutableStateOf<List<RemoteUser>>(emptyList())
        private set
    var estaCarregant by mutableStateOf(false)
        private set

    // Estados para el Diálogo de Error de red
    var mostrarDialogError by mutableStateOf(false)
    var textErrorDialog by mutableStateOf("")

    init {
        // Tan pronto como se crea el ViewModel, hace la llamada asíncrona
        carregarDadesDesDeRepositori()
    }

    private fun carregarDadesDesDeRepositori() {
        // Abrimos una tarea en el hilo secundario (Dispatchers.IO)
        viewModelScope.launch(Dispatchers.IO) {
            estaCarregant = true

            try {
                // Llamamos al repositorio, que decidirá de forma inteligente: Internet o Local
                val resultat = RankingRepository.getRanking(dao)

                // Volvemos al hilo principal para actualizar la pantalla
                withContext(Dispatchers.Main) {
                    rankingMundial = resultat.first // Asignamos la lista de datos

                    // Si el segundo valor no es null, es que hubo error y cargó Room
                    if (resultat.second != null) {
                        textErrorDialog = resultat.second!! // El texto del error
                        mostrarDialogError = true           // Activamos la ventana de aviso
                    }
                    estaCarregant = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    textErrorDialog = "Error inesperado: ${e.message}"
                    mostrarDialogError = true
                    estaCarregant = false
                }
            }
        }
    }

    // Función para que el botón del diálogo lo pueda cerrar
    fun amagarDialog() {
        mostrarDialogError = false
    }
}
