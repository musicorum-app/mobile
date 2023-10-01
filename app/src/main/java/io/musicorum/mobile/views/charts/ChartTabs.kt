package io.musicorum.mobile.views.charts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.LighterGray

@Composable
internal fun ChartTabs(index: MutableState<Int>) {
    val titles = listOf(
        "Artists" to Icons.Rounded.Star,
        "Albums" to Icons.Outlined.Album,
        "Tracks" to Icons.Rounded.MusicNote
    )


    TabRow(
        indicator = {
            if (index.value < it.size) {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(it[index.value]),
                    width = 40.dp
                )
            }
        },
        selectedTabIndex = index.value,
        containerColor = LighterGray
    ) {
        titles.forEachIndexed { i, v ->
            Tab(
                selected = index.value == i,
                onClick = { index.value = i },
                text = { Text(v.first) },
                icon = { Icon(v.second, null) },
                selectedContentColor = Color.White,
                unselectedContentColor = ContentSecondary
            )
        }
    }
}