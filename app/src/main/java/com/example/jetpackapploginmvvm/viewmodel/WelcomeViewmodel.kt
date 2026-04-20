package com.example.jetpackapploginmvvm.viewmodel

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModel
import com.example.jetpackapploginmvvm.model.api.RemoteUser
import com.example.jetpackapploginmvvm.model.api.RetrofitClient
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.jetpackapploginmvvm.model.UserDao
import com.example.jetpackapploginmvvm.model.api.RankingRepository


// S10 Necessitem rebre el DAO
class WelcomeViewModel(private val dao: UserDao) : ViewModel() {
    // L'estat de la pantalla: una llista buida al principi
    var rankingMundial by mutableStateOf<
            List<RemoteUser>>(emptyList())
        private set

    var estaCarregant by mutableStateOf(false)
        private set

    // ESTATS PEL DIÀLEG D'ERROR (L'equivalent a JOptionPane)
    var mostrarDialogError by mutableStateOf(false)
    var textErrorDialog by mutableStateOf("")

    init {
        // Tan bon punt es crea el "cambrer", fa la trucada
        carregarDadesDesDeRepositori()
    }

    private fun carregarDadesDesDeRepositori() {
        // 1. El cambrer obre una comanda asíncrona
        viewModelScope.launch(Dispatchers.IO) {
            estaCarregant = true
            // Cridem al repositori, que farà la màgia de decidir Internet o Local
            val resultat = RankingRepository.getRanking(dao)

            withContext(Dispatchers.Main) { // Fil principal resposta
                rankingMundial = resultat.first // La llista de dades
                if (resultat.second != null) { // Si hi ha error
                    textErrorDialog = resultat.second!!
                    // !! perquè asseguro al compilador que no és null.
                    mostrarDialogError = true // Estat on del diàleg
                }
                estaCarregant = false
            }
        }
    }
    fun amagarDialog() { // Funció per tancar estat on del diàleg
        mostrarDialogError = false
    }
}