package com.example.jetpackapploginmvvm.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackapploginmvvm.model.api.RemoteUser

@Composable
fun ScreenWelcome(
    username: String,
    ranking: List<RemoteUser>,
    isLoading: Boolean,
    mostrarDialogError: Boolean,
    missatgeError: String,
    onDismissDialog: () -> Unit,
    onLogoutClick: () -> Unit,
    onStartGame: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Mensaje de bienvenida
            Text(
                text = "¡Hola, $username!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. El bloque del Ranking (LazyColumn)
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 300.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Si está cargando desde Internet, mostramos la rueda
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    } else {
                        // Si ya ha terminado de cargar, pintamos la lista
                        Text(
                            text = "Top Mundial:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        LazyColumn {
                            items(items = ranking) { user ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = user.username,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${user.highScore} pts",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Botones de acción
            Button(
                onClick = onStartGame,
                modifier = Modifier.fillMaxWidth(0.7f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Jugar al Ahorcado", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Cambiar Usuario")
            }

            // 4. El Diálogo de Error
            if (mostrarDialogError) {
                AlertDialog(
                    onDismissRequest = onDismissDialog,
                    title = { Text(text = "Error de Red") },
                    text = { Text(text = missatgeError) },
                    confirmButton = {
                        TextButton(onClick = onDismissDialog) {
                            Text("De acuerdo")
                        }
                    }
                )
            }
        }
    }
}
