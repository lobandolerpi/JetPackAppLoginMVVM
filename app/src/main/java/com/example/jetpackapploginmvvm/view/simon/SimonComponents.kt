package com.example.jetpackapploginmvvm.view.simon

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.jetpackapploginmvvm.model.GameColor

@Composable
fun SimonButton(
    gameColor: GameColor,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(gameColor.color)
            .clickable ( onClick = onClick ) // Sense lambda, el paràmetre es la funció que rebo com a paràmetre.
            //.clickable { onClick() } // Amb lambda
    )
}