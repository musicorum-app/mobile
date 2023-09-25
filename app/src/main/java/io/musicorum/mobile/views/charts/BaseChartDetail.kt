package io.musicorum.mobile.views.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.models.ResourceEntity
import io.musicorum.mobile.router.Routes
import io.musicorum.mobile.ui.theme.KindaBlack
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseChartDetail(
    index: Int,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val nav = LocalNavigation.current
    val tabIndex = remember { mutableIntStateOf(index) }
    val showBottomSheet = remember { mutableStateOf(false) }
    val sysUiController = rememberSystemUiController()

    val busy = viewModel.busy.observeAsState().value
    val artists = viewModel.artists.observeAsState().value
    val albums = viewModel.albums.observeAsState().value
    val tracks = viewModel.tracks.observeAsState().value
    val fetchPeriod = viewModel.period.observeAsState(FetchPeriod.WEEK)

    val currentViewMode = viewModel.viewMode.observeAsState(ViewMode.List)

    if (showBottomSheet.value) {
        PeriodBottomSheet(state = showBottomSheet) {
            viewModel.updatePeriod(it)
        }
    }


    LaunchedEffect(key1 = tabIndex.intValue) {
        val entity = when (tabIndex.intValue) {
            0 -> ResourceEntity.Artist
            1 -> ResourceEntity.Album
            2 -> ResourceEntity.Track
            else -> ResourceEntity.Track
        }
        viewModel.refetch(entity)
    }

    val viewModeIcon = if (currentViewMode.value == ViewMode.Grid) {
        Icons.Rounded.List
    } else Icons.Rounded.GridView

    fun updateViewMode() {
        if (currentViewMode.value == ViewMode.List) {
            viewModel.viewMode.value = ViewMode.Grid
        } else {
            viewModel.viewMode.value = ViewMode.List
        }
    }

    val appBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = LighterGray
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = appBarColors,
                title = { Text("Charts") },
                actions = {
                    IconButton(onClick = { updateViewMode() }) {
                        Icon(viewModeIcon, null, tint = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { nav?.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                }
            )

        },
        floatingActionButton = {
            CollageFab(
                period = fetchPeriod.value,
                entity = entityResolver(tabIndex.intValue)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .background(KindaBlack)
                .fillMaxSize()
        ) {
            ChartTabs(tabIndex)

            if (busy == true) {
                CenteredLoadingSpinner()
            } else {
                if (tabIndex.intValue == 0) {
                    ArtistChartDetail(artists = artists, currentViewMode.value)
                }
                if (tabIndex.intValue == 1) {
                    AlbumChartDetail(albums = albums, currentViewMode.value)
                }
                if (tabIndex.intValue == 2) {
                    TrackChartDetail(tracks = tracks, currentViewMode.value)
                }
            }
        }
    }
}

@Composable
private fun CollageFab(period: FetchPeriod, entity: ResourceEntity) {
    val nav = LocalNavigation.current

    FloatingActionButton(
        onClick = { nav?.navigate(Routes.collage(period = period, entity = entity)) },
        containerColor = MostlyRed
    ) {
        Icon(Icons.Filled.AutoAwesomeMosaic, null)
    }
}

internal fun entityResolver(index: Int): ResourceEntity {
    return when (index) {
        0 -> ResourceEntity.Artist
        1 -> ResourceEntity.Album
        2 -> ResourceEntity.Track
        else -> ResourceEntity.Track
    }
}
