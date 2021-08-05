package com.musicorumapp.mobile.ui.components

import android.icu.text.CompactDecimalFormat
import android.icu.text.NumberFormat
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicorumapp.mobile.ui.theme.MusicorumTheme
import com.musicorumapp.mobile.ui.theme.PaddingSpacing
import com.musicorumapp.mobile.ui.theme.SecondaryTextColor

@Composable
fun StatItem(
    title: String,
    value: Int?,
    compactFormatInstance: CompactDecimalFormat

) {
    Column {
        Text(
            if (value != null) compactFormatInstance.format(value) else "-",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            modifier = Modifier.height(31.dp)
        )
        Text(
            title,
            fontSize = 14.sp,
            color = SecondaryTextColor,
        )
    }
}

@Composable
fun Stats(
    stats: Map<String, Int?> = emptyMap()
) {
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]
    val instance = CompactDecimalFormat.getInstance(locale, CompactDecimalFormat.CompactStyle.SHORT)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingSpacing.HorizontalMainPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        stats.forEach {
            StatItem(title = it.key, value = it.value, compactFormatInstance = instance)
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 100)
@Composable
fun StatsPreview() {
    MusicorumTheme {
        Scaffold {
            Stats(
                mapOf(
                    "Foo" to 20,
                    "Example" to 100000,
                    "Big" to 878957623
                )
            )
        }
    }
}
