package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StarryBackground
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var loadingProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val totalTime = 2500L
        val interval = 50L
        val steps = totalTime / interval
        for (i in 0..steps) {
            loadingProgress = i.toFloat() / steps
            delay(interval)
        }
        onTimeout()
    }

    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing))
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        StarryBackground()

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                // Neon Ring
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer { rotationZ = rotation }
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(NeonCyan, NeonPurple, NeonPink, NeonCyan)
                            ),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(Color.Black, shape = CircleShape)
                )

                // App Name
                Text(
                    "حِسْبَة",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonCyan,
                    modifier = Modifier.graphicsLayer {
                        shadowElevation = 20f
                        ambientShadowColor = NeonCyan
                        spotShadowColor = NeonCyan
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                "H I S B A",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.graphicsLayer(alpha = 0.99f).drawWithCache {
                    val brush = Brush.linearGradient(listOf(NeonCyan, NeonPurple, NeonPink))
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush, blendMode = BlendMode.SrcAtop)
                    }
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = loadingProgress,
                modifier = Modifier
                    .width(200.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = NeonCyan,
                trackColor = SurfaceDark
            )
        }

        // Developer attribution
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .background(SurfaceDark, shape = RoundedCornerShape(20.dp))
                .border(1.dp, SurfaceDarkNeon, RoundedCornerShape(20.dp))
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Text(
                "Eng. Mohammed Tarek © 2026",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
