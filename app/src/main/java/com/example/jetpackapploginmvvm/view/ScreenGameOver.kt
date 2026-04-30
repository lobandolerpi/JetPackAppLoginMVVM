package com.example.jetpackapploginmvvm.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jetpackapploginmvvm.navigation.AppScreens

@Composable
fun ScreenGameOver(navController: NavController, resultado: String, username: String, onRestart: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = resultado,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = if (resultado.contains("Ganaste", ignoreCase = true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = {
                    onRestart()
                    navController.navigate(AppScreens.Welcome.createRoute(username)) {
                        popUpTo(AppScreens.Login.route) { inclusive = false }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.6f).height(56.dp)
            ) {
                Text("Volver al Inicio", fontSize = 18.sp)
            }
        }
    }
}
