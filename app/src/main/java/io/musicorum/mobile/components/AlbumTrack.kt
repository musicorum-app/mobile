package io.musicorum.mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.ui.theme.BodyLarge
import io.musicorum.mobile.ui.theme.KindaBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumTrack(position: Int, name: String) {
    ListItem(
        headlineText = { Text(name, style = BodyLarge) },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(KindaBlack),
                contentAlignment = Alignment.Center
            ) {
                Text(position.toString(), style = BodyLarge)
            }
        }
    )
}