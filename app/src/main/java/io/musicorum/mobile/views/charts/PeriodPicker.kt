package io.musicorum.mobile.views.charts

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.R
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed

@Composable
fun PeriodPicker(
    showDivider: Boolean = true,
    selectedPeriod: FetchPeriod,
    onPeriodChanged: (FetchPeriod) -> Unit
) {
    val periods = listOf(
        FetchPeriod.WEEK to stringResource(R.string.last_7_days),
        FetchPeriod.MONTH to stringResource(R.string.last_30_days),
        FetchPeriod.TRIMESTER to stringResource(R.string.last_90_days),
        FetchPeriod.SEMESTER to stringResource(R.string.last_6_months),
        FetchPeriod.YEAR to stringResource(R.string.last_12_months),
        FetchPeriod.OVERALL to stringResource(R.string.overall)
    )
    val mod = Modifier
        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        .background(LighterGray)
        .height(45.dp)
        .fillMaxWidth()

    Column {
        Box(modifier = mod) {
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Spacer(modifier = Modifier.width(15.dp))
                }

                periods.forEach { fp ->
                    item {
                        PeriodComponent(
                            period = fp,
                            active = selectedPeriod == fp.first,
                            onPeriodChanged
                        )
                    }
                }
            }
        }
        if (showDivider) {
            HorizontalDivider(thickness = 1.dp, color = Color.White)
        }
    }
}

@Composable
private fun PeriodComponent(
    period: Pair<FetchPeriod, String>,
    active: Boolean,
    onClick: (FetchPeriod) -> Unit
) {
    val activeMod = Modifier
        .padding(end = 15.dp)
        .clip(RoundedCornerShape(100))
        .background(MostlyRed)
        .padding(horizontal = 9.dp, vertical = 1.dp)

    val normalMod = Modifier
        .padding(end = 15.dp)
        .clickable { onClick(period.first) }

    AnimatedContent(targetState = active, label = "period_picker") {
        when (it) {
            true -> Box(modifier = activeMod, contentAlignment = Alignment.Center) {
                Text(text = period.second)
            }

            false -> Box(modifier = normalMod, contentAlignment = Alignment.Center) {
                Text(text = period.second)
            }
        }
    }
}