package com.example.jetpackapploginmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackapploginmvvm.model.User
import com.example.jetpackapploginmvvm.model.UserDao
import com.example.jetpackapploginmvvm.model.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Estat inicial per la UI de Login.
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val message: String = "",
    val errorMsg: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false // Nueva bandera para navegar
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onUsernameChange(input: String) {
        _uiState.value = _uiState.value.copy(username = input, message = "", errorMsg = "")
    }

    fun onPasswordChange(input: String) {
        _uiState.value = _uiState.value.copy(password = input, message = "", errorMsg = "")
    }

    // Función para Registrarse
    fun onRegisterClick(dao: UserDao) {
        val current = _uiState.value
        if (current.username.isNotBlank() && current.password.isNotBlank()) {
            _uiState.value = current.copy(isLoading = true)
            // 1. Abrimos corrutina en hilo secundario (Dispatchers.IO)
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val repository = UserRepository(dao)
                    val isAdded = repository.addUser(User(current.username, current.password))

                    // 2. Volvemos al hilo principal para mostrar el mensaje (Dispatchers.Main)
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            _uiState.value = _uiState.value.copy(
                                errorMsg = "OK! Usuario registrado. Ya puedes entrar.",
                                username = "",
                                password = "",
                                message = "",
                                isLoading = false
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                errorMsg = "ERROR: El usuario ya existe",
                                message = "",
                                isLoading = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            errorMsg = "ERROR CRÍTICO: ${e.message}",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    // Función para Iniciar Sesión
    fun onLoginClick(dao: UserDao) {
        val current = _uiState.value
        _uiState.value = current.copy(isLoading = true)
        
        // 1. Abrimos corrutina en hilo secundario (Dispatchers.IO)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val repository = UserRepository(dao)
                val storedUser = repository.getUser(current.username)

                // 2. Volvemos al hilo principal para actualizar la pantalla (Dispatchers.Main)
                withContext(Dispatchers.Main) {
                    if (storedUser == null) {
                        _uiState.value = _uiState.value.copy(
                            errorMsg = "ERROR: Usuario no encontrado",
                            message = "",
                            isLoading = false
                        )
                    } else if (storedUser.password == current.password) {
                        // Contraseña correcta: Damos la orden de navegar
                        _uiState.value = _uiState.value.copy(
                            loginSuccess = true,
                            errorMsg = "",
                            isLoading = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            errorMsg = "ERROR: Contraseña incorrecta",
                            message = "",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMsg = "ERROR CRÍTICO: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    // Función para limpiar la bandera de éxito después de navegar
    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(loginSuccess = false)
    }
}
