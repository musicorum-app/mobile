package io.musicorum.mobile.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import io.musicorum.mobile.LocalNavigation
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.R
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.ui.theme.SkeletonSecondaryColor
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.PeriodResolver
import io.musicorum.mobile.utils.Placeholders
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import io.musicorum.mobile.utils.getDarkenGradient
import io.musicorum.mobile.viewmodels.ChartsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Charts(model: ChartsViewModel = viewModel()) {
    val period = remember { mutableStateOf(FetchPeriod.WEEK) }
    val user = LocalUser.current ?: return
    val ctx = LocalContext.current
    val userColor = model.preferredColor.observeAsState().value
    val topArtists = model.topArtists.observeAsState().value
    val topAlbums = model.topAlbums.observeAsState().value
    val topTracks = model.topTracks.observeAsState().value
    val showBottomSheet = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = period.value) {
        model.invalidate()
        model.getColor(user.user.bestImageUrl, ctx)
        model.getTopArtists(user.user.name, period.value)
        model.getTopAlbums(user.user.name, period.value)
        model.getTopTracks(user.user.name, period.value)
    }

    if (showBottomSheet.value) {
        PeriodBottomSheet(state = showBottomSheet, period = period)
    }

    if (userColor == null) return CenteredLoadingSpinner()
    val userGradient = getDarkenGradient(userColor)

    Scaffold(floatingActionButton = { CollageFab() }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Charts", style = Typography.displayMedium)
                    Box(
                        modifier = Modifier
                            .background(EvenLighterGray, RoundedCornerShape(15.dp))
                            .clickable { showBottomSheet.value = true }
                    ) {
                        Text(
                            text = PeriodResolver.resolve(period.value),
                            modifier = Modifier.padding(vertical = 3.dp, horizontal = 7.dp)
                        )
                    }
                }
                FilledIconButton(onClick = { showBottomSheet.value = true }) {
                    Icon(Icons.Rounded.DateRange, null)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(15.dp)) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(userGradient.asReversed()),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(start = 10.dp)
                        .fillMaxWidth()
                        .height(70.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = topTracks?.attributes?.total ?: ".......",
                            style = Typography.headlineLarge,
                            modifier = Modifier.placeholder(
                                topTracks == null,
                                color = SkeletonSecondaryColor,
                                highlight = PlaceholderHighlight.shimmer(),
                                shape = RoundedCornerShape(5.dp)
                            )
                        )
                        Text(text = "scrobbles", style = Typography.titleMedium)
                    }
                    Image(
                        painter = painterResource(id = R.drawable.chart_decorations),
                        contentDescription = null,
                        modifier = Modifier
                            .align(CenterEnd)
                            .alpha(.15f)
                    )
                }
            }

            if (topArtists == null || topAlbums == null || topTracks == null) {
                CenteredLoadingSpinner()
            } else {
                val topArtist = topArtists[0]
                ChartComponentBox(
                    leadImage = topArtist.bestImageUrl,
                    trailDetail = Icons.Rounded.Star,
                    shape = CircleShape,
                    artist = topArtist.name,
                    scrobbleCount = topArtist.playCount,
                    top = "artists",
                    album = null,
                    innerData = topArtists.drop(1).fold(mutableListOf()) { list, artist ->
                        list.add(ChartData(artist.name, artist.bestImageUrl, artist.playCount))
                        list
                    }
                )
                Spacer(modifier = Modifier.height(70.dp))
                ChartComponentBox(
                    leadImage = topAlbums[0].bestImageUrl,
                    trailDetail = Icons.Outlined.Album,
                    shape = RoundedCornerShape(6.dp),
                    artist = topAlbums[0].name,
                    scrobbleCount = topAlbums[0].playCount?.toInt() ?: 0,
                    album = null,
                    top = "albums",
                    innerData = topAlbums.drop(1).fold(mutableListOf()) { list, album ->
                        list.add(
                            ChartData(
                                album.name,
                                album.bestImageUrl,
                                album.playCount?.toInt() ?: 0
                            )
                        )
                        list
                    }
                )
                Spacer(modifier = Modifier.height(70.dp))
                ChartComponentBox(
                    leadImage = topTracks.tracks[0].bestImageUrl,
                    trailDetail = Icons.Rounded.MusicNote,
                    shape = RoundedCornerShape(6.dp),
                    artist = topTracks.tracks[0].name,
                    scrobbleCount = topTracks.tracks[0].playCount?.toInt() ?: 0,
                    album = null,
                    top = "tracks",
                    innerData = topTracks.tracks.drop(1).fold(mutableListOf()) { list, track ->
                        list.add(
                            ChartData(
                                track.name,
                                track.bestImageUrl,
                                track.playCount?.toInt() ?: 0
                            )
                        )
                        list
                    }
                )
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}

@Composable
fun CollageFab() {
    val nav = LocalNavigation.current
    FloatingActionButton(onClick = { nav?.navigate("chartCollage") }, containerColor = MostlyRed) {
        Icon(Icons.Filled.AutoAwesomeMosaic, null)
    }
}

@Composable
fun ChartComponentBox(
    leadImage: String,
    trailDetail: ImageVector,
    shape: Shape,
    artist: String,
    scrobbleCount: Int,
    album: String?,
    innerData: List<ChartData>?,
    top: String
) {
    val vibrant = remember { mutableStateOf(Color.Gray) }
    val vibrantState = animateColorAsState(targetValue = vibrant.value)
    val ctx = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        val bmp = getBitmap(leadImage, ctx)
        val palette = createPalette(bmp)
        palette.getVibrantColor(Color.Gray.toArgb())
        vibrant.value = Color(palette.getVibrantColor(Color.Gray.toArgb()))
    }

    val gradient = getDarkenGradient(vibrantState.value).asReversed()

    Row(
        modifier = Modifier.padding(start = 20.dp, end = 3.dp),
        verticalAlignment = CenterVertically,
    ) {
        Icon(trailDetail, null)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "Top $top", style = Typography.headlineSmall)
        Spacer(modifier = Modifier.weight(1f, true))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Rounded.ChevronRight, null)
        }
    }

    Box(
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = CenterVertically,
            modifier = Modifier
                .background(
                    Brush.linearGradient(gradient),
                    RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                .zIndex(1F)
        ) {
            AsyncImage(
                model = defaultImageRequestBuilder(url = leadImage),
                contentDescription = null,
                placeholder = Placeholders.ARTIST.asPainter(),
                modifier = Modifier
                    .clip(shape)
                    .size(50.dp)
                    .shadow(50.dp)
            )
            Column {
                Text(text = artist, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                album?.let {
                    Text(text = it, style = Typography.labelMedium)
                }
                Text("$scrobbleCount scrobbles", style = Typography.labelSmall)
            }
            Spacer(Modifier.weight(1f))
            Icon(
                trailDetail,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .rotate(-15f)
                    .alpha(.25f),
                tint = LighterGray
            )
        }
        Column(
            modifier = Modifier
                .offset(y = 60.dp)
                .background(LighterGray, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            innerData?.let { list ->
                list.forEachIndexed { i, data ->
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = CenterVertically) {
                        Text(
                            text = "${i + 2}",
                            style = Typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        AsyncImage(
                            model = defaultImageRequestBuilder(url = data.image),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(shape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = data.name)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = data.count.toString(),
                            style = Typography.labelMedium,
                            color = ContentSecondary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodBottomSheet(state: MutableState<Boolean>, period: MutableState<FetchPeriod>) {
    ModalBottomSheet(onDismissRequest = { state.value = false }, containerColor = LighterGray) {
        FetchPeriod.values().forEach {
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    state.value = false
                    period.value = it
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

data class ChartData(
    val name: String,
    val image: String,
    val count: Int
)
