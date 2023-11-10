package io.musicorum.mobile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Launch
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.ui.theme.Typography

@Composable
fun RewindCard(description: String, onOpen: () -> Unit) {
    val expandedState = rememberSaveable {
        mutableStateOf(true)
    }
    val gradient = Brush.horizontalGradient(listOf(MostlyRed, MostlyRed.copy(alpha = .5f)))
    val rotationState = rememberSaveable {
        mutableFloatStateOf(90f)
    }
    val rotation = animateFloatAsState(targetValue = rotationState.floatValue)

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 15.dp)
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .background(gradient)
            .padding(horizontal = 14.dp, vertical = 10.dp)

    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Rewind 2023 on the run!", style = Typography.headlineSmall)
                IconButton(onClick = {
                    if (expandedState.value) {
                        expandedState.value = false
                        rotationState.floatValue = 0f
                    } else {
                        expandedState.value = true
                        rotationState.floatValue = 90f
                    }
                }) {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null,
                        modifier = Modifier.rotate(rotation.value)
                    )
                }
            }
            AnimatedVisibility(visible = expandedState.value) {
                Column {
                    val tonalColors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.White.copy(alpha = .4f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(description, style = Typography.bodyLarge)
                    Spacer(modifier = Modifier.height(10.dp))
                    FilledTonalButton(
                        onClick = onOpen,
                        colors = tonalColors,
                        modifier = Modifier.height(35.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Launch, null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Visit")
                    }
                }
            }
        }
    }
}