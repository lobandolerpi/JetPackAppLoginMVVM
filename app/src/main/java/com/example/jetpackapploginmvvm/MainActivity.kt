package com.example.jetpackapploginmvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetpackapploginmvvm.navigation.AppNavigation
import com.example.jetpackapploginmvvm.ui.theme.AppMVVMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMVVMTheme {
                AppNavigation(
                    // Aquesta funció és global a tota la app, la defineixo al Main i la passo com argument.
                    // així totes les pantalles i viewmodels poden fer-la servir.
                    onCloseApp = ::finalitzarAplicacio
                )
            }
        }
    }

    // Defineixo la funció fora per estalviarme una Lambda.
    private fun finalitzarAplicacio() {
        this.finish();
    }
}
