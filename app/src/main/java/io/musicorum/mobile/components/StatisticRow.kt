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
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.ui.theme.Typography
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
    Column {
        Text(text = formattedNumber, style = Typography.displaySmall)
        Text(text = label, style = Typography.labelMedium, modifier = Modifier.alpha(0.55f))
    }
}
