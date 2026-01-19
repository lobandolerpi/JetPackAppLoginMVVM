package com.example.jetpackapploginmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackapploginmvvm.model.User
import com.example.jetpackapploginmvvm.model.UserRepository
import com.example.jetpackapploginmvvm.navigation.AppScreens
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SimonViewmodel : ViewModel() {
    // SESSIÓ 1 No fel lògica
    // Aquí anirà el control de la seqüència de colors.
}