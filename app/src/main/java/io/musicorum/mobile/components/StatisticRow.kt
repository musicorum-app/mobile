package io.musicorum.mobile.components

import android.icu.text.CompactDecimalFormat
import android.icu.text.CompactDecimalFormat.CompactStyle
import android.icu.text.NumberFormat
import android.icu.util.ULocale
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.musicorum.mobile.ui.theme.Body2
import io.musicorum.mobile.ui.theme.Poppins
import java.util.*

@Composable
fun StatisticRow(short: Boolean, vararg statistics: Pair<String, Long?>) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        statistics.forEach { pair ->
            Statistic(number = pair.second ?: 0, label = pair.first, short = short)
        }
    }
}

private val statistic = androidx.compose.ui.text.TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight(700),
    fontSize = 28.sp
)

@Composable
private fun Statistic(number: Long, label: String, short: Boolean) {
    val formattedNumber = if (short) {
        CompactDecimalFormat.getInstance(
            ULocale.forLocale(Locale.getDefault()),
            CompactStyle.SHORT
        ).format(number)
    } else {
        NumberFormat.getNumberInstance(ULocale.forLocale(Locale.getDefault()))
            .format(number)
    }
    Column(verticalArrangement = Arrangement.spacedBy((-10).dp)) {
        Text(text = formattedNumber, style = statistic)
        Text(text = label, style = Body2, modifier = Modifier.alpha(0.55f))
    }
}
