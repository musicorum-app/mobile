package io.musicorum.mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.musicorum.mobile.ui.theme.AlmostBlack

@Composable
fun Discover() {
    Row(
        Modifier
            .fillMaxSize()
            .background(AlmostBlack),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "This section is coming soon! Stay tuned for updates.",
            textAlign = TextAlign.Center
        )
    }

}