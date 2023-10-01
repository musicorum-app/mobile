package io.musicorum.mobile.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.darkenColor
import io.musicorum.mobile.utils.getDarkenGradient
import org.jsoup.Jsoup

@Composable
fun ItemInformation(palette: Palette?, info: String) {
    val vibrant = palette?.getVibrantColor(EvenLighterGray.toArgb()) ?: EvenLighterGray.toArgb()
    val gradient = getDarkenGradient(Color(vibrant))
    val sheetState = remember { mutableStateOf(false) }
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.White.copy(alpha = .20f)
    )
    val brush = Brush.horizontalGradient(gradient)

    val txt = Jsoup.parse(info)
    txt.select("a[href]").remove()

    if (sheetState.value) {
        InformationSheet(state = sheetState, text = info, palette = palette)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(brush)
            .padding(15.dp)
    ) {
        Column {
            Text(
                text = txt.text().dropLast(1),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = Typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier
                    .align(Alignment.End)
                    .height(30.dp),
                onClick = { sheetState.value = true },
                colors = buttonColors,
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                Text(
                    text = "Read More",
                    style = Typography.labelLarge
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InformationSheet(state: MutableState<Boolean>, text: String, palette: Palette?) {
    val ctx = LocalContext.current
    val doc = Jsoup.parse(text)
    val href = doc.select("a[href]")[0].attributes()["href"]
    doc.select("a[href]").remove()
    val paletteColor = palette?.getVibrantColor(LighterGray.toArgb()) ?: LighterGray.toArgb()
    val containerColor = darkenColor(paletteColor, factor = .30f)
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(href))

    val textColor =
        if (palette?.vibrantSwatch != null && palette.vibrantSwatch?.hsl?.getOrNull(2)!! >= .5f) {
            Color.Black
        } else Color.White

    ModalBottomSheet(
        onDismissRequest = { state.value = false },
        containerColor = containerColor
    ) {
        Text(
            text = "Wiki information",
            style = Typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = textColor
        )

        Text(
            text = doc.text().dropLast(1),
            modifier = Modifier.padding(20.dp),
            color = textColor
        )
        val colors = ButtonDefaults.textButtonColors(
            contentColor = textColor
        )
        TextButton(
            onClick = { ctx.startActivity(intent) },
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
            colors = colors
        ) {
            Icon(Icons.Rounded.OpenInNew, null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "More on Last.fm")
        }
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}
