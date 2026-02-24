package com.example.jetpackapploginmvvm.view


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jetpackapploginmvvm.viewmodel.MascotaViewModel

@Composable
fun ScreenMascotaCrear(
    viewModel: MascotaViewModel,
    onMascotaCreada: () -> Unit,
    onBackClick: () -> Unit
) {
    // Estado local para escribir el nombre en el cuadro de texto
    var nombreTexto by remember { mutableStateOf("") }

    Column(

        modifier = Modifier.fillMaxSize().padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Com vols que es digui la teva mascota?", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        // Cuadro para escribir el nombre
        OutlinedTextField(

            value = nombreTexto,
            onValueChange = { nombreTexto = it },
            label = { Text("Nom de la mascota") },
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón para crear
        Button(
            onClick = {
                if (nombreTexto.isNotBlank()) {
                    viewModel.crearMascota(nombreTexto)
                    onMascotaCreada() // Esto nos llevará a la pantalla del juego
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Començar aventura!")
        }

        TextButton(onClick = onBackClick) {
            Text("Tornar enrere")
        }
    }
}