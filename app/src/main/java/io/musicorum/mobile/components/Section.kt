package io.musicorum.mobile.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.ui.theme.Typography

@Composable
fun Section(title: String, textAlign: TextAlign? = null) {
    Text(
        text = title, style = Typography.headlineSmall,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        textAlign = textAlign
    )
}