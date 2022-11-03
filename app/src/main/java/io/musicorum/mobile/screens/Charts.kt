package io.musicorum.mobile.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun Charts() {
    Row(
        Modifier
            .fillMaxSize(), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "This section is coming soon! Stay tuned for updates",
            textAlign = TextAlign.Center
        )
    }

}