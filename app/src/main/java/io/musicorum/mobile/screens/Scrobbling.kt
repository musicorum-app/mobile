package io.musicorum.mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun Scrobbling() {
    Column {
        Column {
            Box(modifier = Modifier.background(Color.Red))
        }
        Column(modifier = Modifier.fillMaxHeight()) {

        }
    }
}