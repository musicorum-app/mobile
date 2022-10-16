package io.musicorum.mobile.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import io.musicorum.mobile.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Discover(nav: NavHostController) {
    Surface {
        Scaffold(bottomBar = { BottomNavBar(current = "Discover", nav = nav) }) {
            Row(Modifier.padding(it)) {
                Text("Deus é mais")
            }
        }
    }
}