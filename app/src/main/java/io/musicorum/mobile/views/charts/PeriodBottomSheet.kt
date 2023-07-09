package io.musicorum.mobile.views.charts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.utils.PeriodResolver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PeriodBottomSheet(
    state: MutableState<Boolean>,
    onChange: (FetchPeriod) -> Unit
) {
    ModalBottomSheet(onDismissRequest = { state.value = false }, containerColor = LighterGray) {
        FetchPeriod.values().forEach {
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    state.value = false
                    onChange(it)
                }
            ) {
                Text(
                    text = PeriodResolver.resolve(it).replaceFirstChar { it.uppercaseChar() },
                    modifier = Modifier.padding(15.dp)
                )
            }
        }
    }
}