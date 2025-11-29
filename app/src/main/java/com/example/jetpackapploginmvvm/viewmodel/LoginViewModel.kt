package com.example.jetpackapploginmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackapploginmvvm.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreens{
    LOGIN,
    WELCOME,
    SETTINGS
}
// Estat inicial per la UI de Login.
// Els tres continguts en blanc.
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val message: String = "",
    val errorMsg: String = "",
    val screenState: AppScreens = AppScreens.LOGIN
)

// sealed: només pot ser un objecte definit a la mateixa llibreria que LoginEvent.
// A dins de la interfície li diem que només accepti CloseApp com a LoginEvent vàlid.
sealed interface LoginEvent {
    data object  CloseApp : LoginEvent
}

// ViewModel és una classe de Kotling per aplicacions.
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


    // Configuració dels events que ens poden canviar la App IMPERATIVAMENT
    // Igual que els estats pero Shared en lloc de States.
    private val _eventFlow = MutableSharedFlow<LoginEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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
                // Alerta, si canvio aquí l'username no el podré recollir per salidar!
                _uiState.value = current.copy(message = "Login Exitós !!", errorMsg = "", password ="", screenState = AppScreens.WELCOME)
            } else {
                _uiState.value = current.copy(message = "", errorMsg =  "ERROR: Credencials invàlides !!")
            }
        }
    }

    fun onLogoutClick(){
        _uiState.value = _uiState.value.copy(message = "", errorMsg = "", username = "", password ="", screenState = AppScreens.LOGIN)
    }

    fun onCloseClick(){
        viewModelScope.launch { _eventFlow.emit(LoginEvent.CloseApp) }
    }
}