package io.musicorum.mobile.views.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed

@Composable
fun PeriodPicker(
    showDivider: Boolean = true,
    selectedPeriod: FetchPeriod,
    onPeriodChanged: (FetchPeriod) -> Unit
) {
    var mod = Modifier
        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        .background(LighterGray)
        .height(45.dp)
        .fillMaxWidth()

    if (showDivider) {
        mod = mod.drawBehind {
            drawLine(
                Color.White,
                Offset.Zero,
                Offset.Infinite,
                5f
            )
        }
    }
    Box(modifier = mod) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Spacer(modifier = Modifier.width(15.dp))
            }

            FetchPeriod.values().forEach { fp ->
                item {
                    PeriodComponent(period = fp, active = selectedPeriod == fp, onPeriodChanged)
                }
            }
        }
    }
}

@Composable
private fun PeriodComponent(period: FetchPeriod, active: Boolean, onClick: (FetchPeriod) -> Unit) {
    val modifier = if (active) {
        Modifier
            .padding(end = 15.dp)
            .clip(RoundedCornerShape(100))
            .background(MostlyRed)
            .padding(horizontal = 9.dp)
    } else Modifier
        .padding(end = 15.dp)
        .clickable { onClick(period) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = period.value)
    }
}