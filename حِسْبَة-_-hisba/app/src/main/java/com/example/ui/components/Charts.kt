package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun NeonPieChart(
    values: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = values.sum()
    val proportions = if (total == 0f) values.map { 0f } else values.map { it / total }
    val sweepAngles = proportions.map { it * 360f }
    
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(values) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(1f, tween(1000))
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = size.minDimension * 0.2f
        var startAngle = -90f
        
        if (total == 0f) {
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
            return@Canvas
        }

        for (i in sweepAngles.indices) {
            val sweepAngle = sweepAngles[i] * animatedProgress.value
            drawArc(
                color = colors.getOrElse(i) { Color.Cyan },
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}
