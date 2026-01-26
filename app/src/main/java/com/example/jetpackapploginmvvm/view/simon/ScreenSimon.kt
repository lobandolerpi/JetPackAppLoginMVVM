package com.example.jetpackapploginmvvm.view.simon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackapploginmvvm.model.GameColor

// Amb això evitarem una lambda.
// el que fa es crear un Botó Simon Button.
@Composable
fun CrearBotoSimon(color: GameColor){
    SimonButton(
        gameColor = color,
        onClick = {
            // TODO: Sessió 2 (Il·luminar)
            // Sessió 2: Cal posar la lògica aquí.
            println("Clicar: ${color.label}")
        }
    )
}

@Composable
fun ScreenSimon(
    // La pantalla és vista, no sap com funciona la nevegació.
    // M'han de passar que fan els botons.
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit
){
    val gridSize = 2 // Mode Facil
    // val gridSize = 3 // Mode dificil.

    // Faig un nou enum, on afago segons la dificultat del joc.
    val activeColors = GameColor.entries.take(gridSize * gridSize)
    // El primer contenidor serà fixe, sempre a dalt de la pantalla
    // Per tant Column
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ){
        // Text pel títol
        Text(
            text="Simon dice",
            fontSize = 32.sp,
            modifier = Modifier.padding(top=32.dp)
            )
        // Aquí alguns jocs no cabrà tot en pantalla
        // per tant és millor un lazy vertical grid
        // per optimitzar memòria.
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridSize),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            // al Grid hi haurà "botons, del meu joc"
            // que son components que tenen com a paràmetre l'enum del color.
            for (itColor in activeColors) {
                item {
                    CrearBotoSimon(color = itColor)
                }
            }

            /*
            // VERSIÓ AVANÇADA (AMB LAMBDES)
            // La funció 'items' fa el bucle per nosaltres.
            items(activeColors) {
                itemColor -> // <--- INICI PRIMERA LAMBDA (EL BUCLE)

                // 1. Què és 'itemColor'?
                // És la VARIABLE que conté la dada d'aquesta volta del bucle.
                // (A literació 1 serà el compoent Vermell de l'enum, a la 2 Groc...).

                // 2. Què fa la fletxa '->'?
                // Separa les dades d'entrada (esquerra) del codi a executar (dreta).

                SimonButton(
                    gameColor = itemColor,
                    onClick = {
                        // <--- INICI SEGONA LAMBDA (L'ACCIÓ)

                        // 3. Per què això també és una lambda?
                        // Perquè és un bloc de codi que NO s'executa ara.
                        // S'envia "empaquetat" dins del botó i s'executarà
                        // només quan l'usuari faci clic en el futur.

                        // TODO: Sessió 2 (Il·luminar)
                        // Sessió 2: Cal posar la lògica aquí.
                        println("Clicar: ${color.label}")
                    }
                )
            }
            */
        }

        Column (
            modifier = Modifier.padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Button(onClick = onBackClick) {
                Text("Tornar al menú")
            }
            Button(onClick = onCloseClick) {
                Text("Tancar App")
            }

        }
    }
}