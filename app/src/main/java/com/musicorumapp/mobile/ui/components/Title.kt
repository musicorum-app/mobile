package com.musicorumapp.mobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import com.musicorumapp.mobile.ui.theme.AppMaterialIcons
import com.musicorumapp.mobile.ui.theme.MusicorumTheme

@Composable
fun Title(
    text: String,
    topPadding: Boolean = true
) {
    Box(modifier = Modifier.statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(AppMaterialIcons.Settings, contentDescription = "Settings")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TitlePreview() {
    MusicorumTheme {
        Scaffold {
            Title(text = "Title text")
        }
    }
}