package com.example.jetpackapploginmvvm.view.simon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackapploginmvvm.viewmodel.ButtonState
import com.example.jetpackapploginmvvm.viewmodel.SimonViewmodel

// Amb això evitarem una lambda.
// el que fa es crear un Botó Simon Button.
@Composable
fun CrearBotoSimon(btnState: ButtonState, viewmodel: SimonViewmodel){
    //Funció que passaré com argument
    fun gestionarClicColor() {
        // S2: La lògica està al viewmodel.
        viewmodel.onColorClick(btnState.color)
        println("Clicar: ${btnState.color.label}")
    }


    SimonButton(
        buttonState = btnState,
        onClick = ::gestionarClicColor // funció com argument.
    )
}

@Composable
fun ScreenSimon(
    // La pantalla és vista, no sap com funciona la nevegació.
    // M'han de passar que fan els botons.
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    viewModel: SimonViewmodel = viewModel() // Injectem el ViewModel
){
    // by    cal importar    import androidx.compose.runtime.getValue
    // S03, ATENCIÓ SI FEM SERVIR FLOW, HEM DEL COLLECTASSTATE
    val state by viewModel.uiState.collectAsState() // Observem l'estat
    val gridSizeX = state.gridSizeX // Mode Facil
    // val gridSize = 3 // Mode dificil.
    val colorBackG = Color(red = 40, green = 0, blue = 40)
    val colorButton = Color(red = 120, green = 50, blue = 120)
    val colorText = Color(red = 240, green = 240, blue = 200)

    // El primer contenidor serà fixe, sempre a dalt de la pantalla
    // Per tant Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colorBackG),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween

    ){
        // by    cal importar    import androidx.compose.runtime.getValue
        //
        val textMsg = state.message
        // Text pel títol
        Text(
            text="Simon diu",
            fontSize = 32.sp,
            color = colorText,
            modifier = Modifier
                .padding(top=16.dp)
                .background(colorBackG),
            )
        // Text filler
        Text(
            text=state.title,
            fontSize = 24.sp,
            color = colorText,
            modifier = Modifier
                .padding(top=8.dp)
                .background(colorBackG),
        )

        // Missatge check
        Text(
            text=state.message,
            minLines = 2, // S04 Força a ocupar sempre l'espai de 2 línies com a mínim
            maxLines = 2,
            fontSize = 20.sp,
            color = colorText,
            modifier = Modifier
                .padding(top=8.dp)
                .background(colorBackG),
            )

        //S03 He afegit aquest botó per separar lògiques
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = colorButton,
                contentColor = colorText
            ),
            onClick = { viewModel.startGame() },
            enabled = !state.isGameStarted  // Habilitat si no juguem o Game Over
        ) {
            Text(if (state.isGameStarted) "Jugant... " else "START")
        }

        // Aquí alguns jocs no cabrà tot en pantalla
        // per tant és millor un lazy vertical grid
        // per optimitzar memòria.
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridSizeX),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .weight(1f)
                .background(colorBackG),
        ) {
            // al Grid hi haurà "botons, del meu joc"
            // que son components que ara tenen estats
            for (stButton in state.buttons) {
                item {
                    CrearBotoSimon(btnState = stButton, viewmodel = viewModel)
                }
            }
        }

        Column (
            modifier = Modifier.padding(bottom = 16.dp)
                .background(colorBackG),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorButton,
                    contentColor = colorText
                ),) {
                Text("Tornar al menú")
            }
            Button(
                onClick = onCloseClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorButton,
                    contentColor = colorText
                ),) {
                Text("Tancar App")
            }

        }
    }
}