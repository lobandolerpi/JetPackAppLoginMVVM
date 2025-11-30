package com.example.jetpackapploginmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackapploginmvvm.model.User
import com.example.jetpackapploginmvvm.navigation.AppScreens
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// Estat inicial per la UI de Login.
// Els tres continguts en blanc.
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val message: String = "",
    val errorMsg: String = "",
    val isLoading: Boolean = false
)

// ViewModel és una classe de Kotlin per aplicacions.
// Aquí estem creant una extensió d'aquesta classe.
class LoginViewModel : ViewModel() {
    // La lògica de la nostra App ha de tenir el Hashmap de clients:
    private val users = mutableMapOf<String, User>()

    // L'estat de l'aplicació és privat, només el viewmodel el pot canviar
    // Però la vista l'ha de poder veure.
    // Per assegurar que la vista veu però no canvia, creem _uiState i uiState.
    private val _uiState = MutableStateFlow(LoginUiState())
    // Iniciem tot en blanc amb el LoginUiState, però serà mutable.
    val uiState = _uiState.asStateFlow() // Aquesta, agafará sempre el valor de la privada


    // Canviar de pantalla és un event, però és únic.
    // És millor fer servir Channel en aquests casos.
    private val _navigationChannel = Channel<String>()
    val navigationChannel = _navigationChannel.receiveAsFlow()

    fun onUsernameChange(input: String) {
        _uiState.value = _uiState.value.copy(input, message = "", errorMsg = "")
    }

    fun onPasswordChange(input: String) {
        _uiState.value = _uiState.value.copy(password = input, message = "", errorMsg = "")
    }

    fun onRegisterClick(){
        val current = _uiState.value
        if (users.containsKey(current.username)) {
            _uiState.value = current.copy(errorMsg = "ERROR: L'usuari ja existeix !!", message = "")
        } else {
            users[current.username] = User(current.username, current.password)
            _uiState.value = current.copy(message = "Usuari registrat correctament !!", username = "", password ="", errorMsg = "")
        }
    }

    fun onLoginClick(){
        val current = _uiState.value
        val storedUser = users[current.username] //Això pot ser null !!
        if (storedUser == null) {
            _uiState.value = current.copy(errorMsg = "ERROR: L'usuari no existeix !!", message = "")
        } else {
            if( storedUser.password == current.password) {
                // ARA, per canviar de pantalla he de crear un event (launch)
                // que envii l'ordre de navegar mitjançant el canal adequat.
                viewModelScope.launch {
                    _navigationChannel.send(AppScreens.Welcome.createRoute(current.username))
                    _uiState.value = LoginUiState() // Netejo camps
                }
            } else {
                _uiState.value = current.copy(message = "", errorMsg =  "ERROR: Credencials invàlides !!")
            }
        }
    }

    // onCloseClick  ARA no és responsabilitat del ViewModel
    // Es una acció del Sistema Operatiu i ho gestiona la Vista.
}