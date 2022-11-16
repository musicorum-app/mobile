package io.musicorum.mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.musicorum.mobile.R
import io.musicorum.mobile.ui.theme.AlmostBlack

@Composable
fun Charts() {
    Row(
        Modifier
            .fillMaxSize()
            .background(AlmostBlack), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(id = R.string.section_coming_soon),
            textAlign = TextAlign.Center
        )
    }

}