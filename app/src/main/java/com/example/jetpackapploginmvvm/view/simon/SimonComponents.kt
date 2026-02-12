package com.example.jetpackapploginmvvm.view.simon

import android.R
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.blur
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.example.jetpackapploginmvvm.model.GameColor
import com.example.jetpackapploginmvvm.viewmodel.ButtonState

@Composable
fun SimonButton(
    buttonState: ButtonState,
    onClick: () -> Unit
){

    // Precalculem colors igual
    val baseColor = buttonState.color.color
    val backGroundColor =
        if ( buttonState.isLit ) {
            val increasedBright = 0.4f
            val maximumBright = 1f
            val baseColor = buttonState.color.color
            baseColor.copy(
                red = (baseColor.red + increasedBright).coerceAtMost(maximumBright),
                green = (baseColor.green + increasedBright).coerceAtMost(maximumBright),
                blue = (baseColor.blue + increasedBright).coerceAtMost(maximumBright)
            )
        } else {
            baseColor
        }

    val shadowElevation = if ( buttonState.isLit ) { 12.dp } else { 4.dp }
    val shadowColor = if ( buttonState.isLit ) { baseColor } else { Color.Black }
    val glowColor = if ( buttonState.isLit ) { baseColor.copy(alpha = 0.15f) } else { Color.Transparent}

    // S03 Lògica d'animació
    // compose calcula els colors intermitjos.
    val animatedBackGColor by animateColorAsState(
        targetValue = backGroundColor,
        animationSpec = tween(200),
        label = "BackGColorAnimation"
    )

    val animatedShadowGColor by animateColorAsState(
        targetValue = shadowColor,
        animationSpec = tween(200),
        label = "BackGColorAnimation"
    )

    val animatedGlowGColor by animateColorAsState(
        targetValue = glowColor,
        animationSpec = tween(200),
        label = "BackGColorAnimation"
    )






    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    )
    // El darrer paràmetre del Box és una lambda
    // la Lambda torna 1 o més més Box que es superposen al primer un a sobre de l'altre.
    {
        if (buttonState.isLit) {
            // CAPA 1, LA BRILLANTOR
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        animatedGlowGColor,
                        shape= RoundedCornerShape(20.dp)
                    )
                    .blur(15.dp)
            )
        }
        // CAPA2, EL BOTÓ DE VERITAT A SOBRE
        Box(
            modifier = Modifier
                .fillMaxSize(0.8f) // El fem una mica més petit per deixar veure el Glow de sota
                .shadow(
                    elevation = shadowElevation,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = animatedShadowGColor,
                    spotColor = animatedShadowGColor
                )
                .clip(RoundedCornerShape(16.dp))
                .background(animatedBackGColor)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (buttonState.isLit) {
                // CAPA3, ENCARA MÉS BLANC PERO TRANSLÚCID AMB alpha
                // És una LAMBDA PER PODER CLICAR A TOT EL BOTÓ I NO NOMÉS AL BLANC
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.4f) // El fem encara  més petit
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        //.clickable(onClick = onClick)
                        .blur(15.dp)
                )
            }
        }


    }
}