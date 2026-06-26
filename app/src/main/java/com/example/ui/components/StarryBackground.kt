package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.random.Random

data class Star(
    var x: Float,
    var y: Float,
    var radius: Float,
    var alpha: Float,
    val speed: Float
)

@Composable
fun StarryBackground(modifier: Modifier = Modifier, enableAnimation: Boolean = true) {
    val starCount = 100
    val stars = remember { mutableStateListOf<Star>() }

    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        if (stars.isEmpty()) {
            for (i in 0 until starCount) {
                stars.add(
                    Star(
                        x = Random.nextFloat() * size.width,
                        y = Random.nextFloat() * size.height,
                        radius = Random.nextFloat() * 2f + 1f,
                        alpha = Random.nextFloat(),
                        speed = Random.nextFloat() * 0.5f + 0.1f
                    )
                )
            }
        }

        for (star in stars) {
            if (enableAnimation) {
                star.y += star.speed
                if (star.y > size.height) {
                    star.y = 0f
                    star.x = Random.nextFloat() * size.width
                }
                star.alpha = (Math.sin((time * star.speed + star.x).toDouble()).toFloat() + 1f) / 2f
            }
            drawCircle(
                color = Color.White.copy(alpha = star.alpha * 0.7f),
                radius = star.radius,
                center = Offset(star.x, star.y)
            )
        }
    }
}
