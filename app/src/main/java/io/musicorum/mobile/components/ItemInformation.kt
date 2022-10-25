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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import io.musicorum.mobile.ui.theme.Body1
import io.musicorum.mobile.ui.theme.LightGray
import io.musicorum.mobile.ui.theme.Poppins
import io.musicorum.mobile.utils.darkenColor

@Composable
fun ItemInformation(palette: Palette?, info: String) {
    val primary = palette?.getVibrantColor(0) ?: LightGray.toArgb()
    val darken = darkenColor(primary, 0.50f)
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.White.copy(alpha = 0.25f)
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Brush.horizontalGradient(listOf(Color(primary), darken)))
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = info,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = Body1
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
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}