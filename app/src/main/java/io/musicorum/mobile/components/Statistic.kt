package io.musicorum.mobile.components

import android.icu.text.CompactDecimalFormat
import android.icu.util.ULocale
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.musicorum.mobile.ui.theme.Body2
import io.musicorum.mobile.ui.theme.Poppins
import java.util.*


private val statistic = androidx.compose.ui.text.TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight(700),
    fontSize = 28.sp
)

@Composable
fun Statistic(number: Long?, label: String) {
    Column {
        val abbreviated =
            CompactDecimalFormat.getInstance(
                ULocale.forLocale(Locale.ENGLISH),
                CompactDecimalFormat.CompactStyle.SHORT
            ).format(number)
        Text(text = abbreviated, style = statistic)
        Text(text = label, style = Body2, modifier = Modifier.alpha(0.55f))
    }
}