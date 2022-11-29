package io.musicorum.mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.getDarkenGradient

@Composable
fun ItemInformation(palette: Palette?, info: String) {
    val vibrant = palette?.getVibrantColor(EvenLighterGray.toArgb()) ?: EvenLighterGray.toArgb()
    val gradient = getDarkenGradient(Color(vibrant))

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.White.copy(alpha = 0.25f)
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Brush.horizontalGradient(gradient))
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = info,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = Typography.bodyLarge
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    colors = buttonColors,
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "READ MORE",
                        style = Typography.labelLarge
                    )
                }
            }
        }
    }
}