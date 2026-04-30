package com.example.jetpackapploginmvvm.view.simon

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackapploginmvvm.viewmodel.ButtonState
import com.example.jetpackapploginmvvm.viewmodel.SimonViewmodel

@Composable
fun CrearBotoSimon(btnState: ButtonState, viewmodel: SimonViewmodel){
    fun gestionarClicColor() {
        viewmodel.onColorClick(btnState.color)
    }

    SimonButton(
        buttonState = btnState,
        onClick = ::gestionarClicColor
    )
}

@Composable
fun ScreenSimon(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    viewModel: SimonViewmodel = viewModel()
){
    val state by viewModel.uiState.collectAsState()
    val gridSizeX = viewModel.uiState.value.gridSizeX

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text="Simon Dice",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top=32.dp)
                )
                
                Text(
                    text="Nivell 1",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top=8.dp)
                )

                Card(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(0.8f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text=state.message,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = { viewModel.startGame() },
                enabled = !state.isGameStarted,
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(0.5f)
            ) {
                Text(if (state.isGameStarted) "Jugando..." else "START")
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(gridSizeX),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                for (stButton in state.buttons) {
                    item {
                        CrearBotoSimon(btnState = stButton, viewmodel = viewModel)
                    }
                }
            }

            Column (
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Button(onClick = onBackClick, modifier = Modifier.fillMaxWidth(0.6f)) {
                    Text("Volver al menú")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onCloseClick) {
                    Text("Cerrar App", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
