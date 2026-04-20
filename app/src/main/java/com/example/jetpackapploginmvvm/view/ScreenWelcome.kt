package com.example.jetpackapploginmvvm.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

// COM PINTO LA PANTALLA?

@Composable
fun ScreenWelcome(
    username: String,
    ranking: List<RemoteUser>,
    isLoading: Boolean,
    onLogoutClick: () -> Unit,
    onCloseClick: () -> Unit,
    onStartGame:() -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxSize().padding(16.dp)
            .background(Color.Yellow),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Hola, $username!",
            fontSize = 24.sp
        )
        Column(modifier = Modifier
            .padding(16.dp)
            .background(Color.LightGray),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            if (isLoading) {
                CircularProgressIndicator() // El cercle de càrrega
            } else {
                Text(
                    text="Top Mundial:" ,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 4.dp  )
                )
                LazyColumn {
                    items(items= ranking) { user ->
                        Text(
                            text = "${user.username}: ${user.highScore} pts",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(vertical = 4.dp) 
                        )
                    }
                }
            }
        }


        Button(
            onClick = onStartGame,
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            Text(
                text="Jugar al Simon",
                fontSize = 20.sp)
        }

        Button(onClick = onLogoutClick) {Text("Canviar Usuari")}

        Button(onClick = onCloseClick) {Text("Tancar")}
    }
}


