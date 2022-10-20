package io.musicorum.mobile.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import io.musicorum.mobile.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Scrobbling(nav: NavHostController) {
    Scaffold(bottomBar = { BottomNavBar(nav) }) {
        Row(
            Modifier
                .padding(it)
                .fillMaxSize(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "This section is coming soon! Stay tuned for updates",
                textAlign = TextAlign.Center
            )
        }
    }
}