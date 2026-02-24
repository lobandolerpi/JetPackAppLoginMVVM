package com.example.jetpackapploginmvvm.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackapploginmvvm.R
import com.example.jetpackapploginmvvm.viewmodel.MascotaViewModel
import kotlinx.coroutines.delay

@Composable
fun ScreenMascotaJoc(
    viewModel: MascotaViewModel,
    onMascotaMorta: () -> Unit,
    onBackClick: () -> Unit
) {
    val mascota = viewModel.mascota

    //Con esto compruebo si la mascota murio
    LaunchedEffect(Unit) {
        while (true) {

            if (!viewModel.comprobarSiSigueViva()) {


                onMascotaMorta()
            }
            delay(5000) // Espera 5 segundos antes de volver a mirar el reloj

        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Aqui miro cual es el nombre del demonio
        Text(
            text = mascota?.nom ?: "Demonio", //Esto es lo tipico que mira si exsiste si es null o no
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Si esta comiendo (el boton comer) aparece el segundo dibujo sino el standard
        val imagenId = if (viewModel.estaComiendo) {
            R.drawable.demonio_comiendo //Estos son dibujos que he puesto yo
        } else {
            R.drawable.demonio //Este tambien
        }

        Image(
            painter = painterResource(id = imagenId),
            contentDescription = "El meu demoni",
            modifier = Modifier.size(250.dp) //Tamaño de la imagen

        )

        Spacer(modifier = Modifier.height(40.dp))

        //botoon de comer
        Button(
            onClick = { viewModel.darDeComer() },
            modifier = Modifier.height(60.dp).fillMaxWidth(0.6f)
        ) {
            Text("Donar de menjar", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = onBackClick) {
            Text("Sortir al menú")
        }
    }
}