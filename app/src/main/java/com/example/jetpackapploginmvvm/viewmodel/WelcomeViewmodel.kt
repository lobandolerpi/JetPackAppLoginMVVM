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

class WelcomeViewModel : ViewModel() {
    // L'estat de la pantalla: una llista buida al principi
    var rankingMundial by mutableStateOf<
            List<RemoteUser>>(emptyList())
        private set

    var estaCarregant by mutableStateOf(false)
        private set

    init {
        // Tan bon punt es crea el "cambrer", fa la trucada
        carregarDadesDesDAPI()
    }

    private fun carregarDadesDesDAPI() {
        // 1. El cambrer obre una comanda asíncrona
        viewModelScope.launch(Dispatchers.IO) {
            estaCarregant = true
            // 2. L'enviem al cuiner (fil secundari)
            try {
                // 3. Truquem al proveïdor extern.
                // El cuiner es SUSPÈN fins que arriba el JSON.
                val resposta =
                    RetrofitClient.apiService.getRankingMundial()

                // 4. Han arribat! Tornem al cambrer per repintar la UI
                withContext(Dispatchers.Main) {
                    rankingMundial = resposta
                    estaCarregant = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    estaCarregant = false
                    // Aquí podríem gestionar l'error de xarxa
                }
            }
        }
    }
}