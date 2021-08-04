package com.musicorumapp.mobile.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.musicorumapp.mobile.ui.theme.SkeletonPrimaryColor
import com.musicorumapp.mobile.ui.theme.SkeletonSecondaryColor
import kotlin.math.pow

private val PulsatingEasing = Easing { x ->
    if (x < 0.5) 4 * x * x * x else 1 - (-2 * x + 2).pow(3) / 2
}

@Composable
fun PulsatingSkeleton(
    modifier: Modifier = Modifier
        .width(20.dp)
        .height(20.dp)
) {
    val animation = rememberInfiniteTransition()

    val color by animation.animateColor(
        initialValue = SkeletonPrimaryColor,
        targetValue = SkeletonSecondaryColor,
        animationSpec = infiniteRepeatable(
            animation = tween(
                1000, easing = PulsatingEasing, delayMillis = 500
            ),
            repeatMode = RepeatMode.Reverse
        )
    )


    Box(
        modifier = Modifier
            .composed { modifier }
            .background(color)
            .clickable {
                println("click")
            }
    )
}