package com.example.jetpackapploginmvvm.model

data class Mascota( //Clase mascota que de momento solo tiene
    //Comprobacion de si esta muerto, la ultima vez que comio y su nombre
    val nom: String = "",
    val ultimaVegadaQueVaMenjar: Long = 0L,
    val estaViva: Boolean = true
)