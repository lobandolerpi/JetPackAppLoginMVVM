package com.example.jetpackapploginmvvm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Afegim l'anotació Entity i li donem nom a la taula
@Entity(tableName = "users")
data class User (
    // Definim la clau primària. El nom d'usuari no es pot repetir!
    @PrimaryKey
    val username: String,
    val password: String,
    val highScore: Int = 0
)