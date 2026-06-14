package com.example.jetpackapploginmvvm.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackapploginmvvm.model.api.RemoteUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign

// COM PINTO LA PANTALLA?

@Composable
fun ScreenCredits(
    onGoBackClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    Column (
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize().padding(16.dp)
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "CRÈDITS",
            fontSize = 30.sp,
            color = Color.White,
        )
        Text(
            text = " ",
            fontSize = 30.sp,
            color = Color.White,
        )

        Text(
            text = "L'aplicació està creada per:",
            fontSize = 24.sp,
            color = Color.White,
        )
        Text(
            text = " ",
            fontSize = 4.sp,
            color = Color.White,
        )
        Text(
            text = "@lobandolerpi",
            fontSize = 28.sp,
            color = Color.White,
        )
        Text(
            text = " ",
            fontSize = 4.sp,
            color = Color.White,
        )
        Text(
            text = "Com a exercici de l'",
            fontSize = 24.sp,
            color = Color.White,
        )
        Text(
            text = " ",
            fontSize = 4.sp,
            color = Color.White,
        )
        Text(
            text = "Institut el Calamot",
            fontSize = 28.sp,
            color = Color.White,
        )
        Text(
            text = " ",
            fontSize = 40.sp,
            color = Color.White,
        )
        Text(
            text = "Material extern utilitzat:",
            fontSize = 24.sp,
            color = Color.White,
        )
        Text(
            text = " ",
            fontSize = 4.sp,
            color = Color.White,
        )
        Text(
            text = "Musica: ",
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Left
        )
        Text(
            text = "- Percussion intro drums",
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Left
        )
        Text(
            text = "     by mrclaps - via Pixabay",
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Left
        )

        Text(
            text = " ",
            fontSize = 30.sp,
            color = Color.White,
        )

        Button(
            onClick = onGoBackClick,
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            Text(
                text = "Tornar",
                fontSize = 20.sp
            )
        }
        Button(onClick = onCloseClick) {Text("Tancar")}
    }
}



