package io.musicorum.mobile.views.charts

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import io.musicorum.mobile.models.ResourceEntity
import io.musicorum.mobile.router.Routes
import io.musicorum.mobile.ui.theme.ContentSecondary
import io.musicorum.mobile.ui.theme.LighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.ui.theme.SkeletonSecondaryColor
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.Placeholders
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import io.musicorum.mobile.utils.getDarkenGradient
import io.musicorum.mobile.viewmodels.ChartsViewModel

@Composable
fun Charts() {
    val model: ChartsViewModel = viewModel()
    val period = model.period.observeAsState()
    val user = LocalUser.current ?: return
    val ctx = LocalContext.current
    val userColor = model.preferredColor.observeAsState(Color.Gray).value
    val topArtists = model.topArtists.observeAsState().value
    val topAlbums = model.topAlbums.observeAsState().value
    val topTracks = model.topTracks.observeAsState().value
    val showBottomSheet = remember { mutableStateOf(false) }
    val busy = model.busy.observeAsState().value!!

    LaunchedEffect(Unit) {
        model.getColor(user.user.bestImageUrl, ctx)
    }

    if (showBottomSheet.value) {
        PeriodBottomSheet(state = showBottomSheet) {
            model.updatePeriod(it)
        }
    }

    val userGradient = getDarkenGradient(userColor)

    Scaffold(floatingActionButton = { CollageFab() }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxHeight(.94f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Charts",
                    style = Typography.displaySmall,
                    modifier = Modifier.padding(20.dp)
                )

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
                                    visible = busy,
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

                if (busy) {
                    CenteredLoadingSpinner()
                } else {
                    val topArtist = topArtists?.getOrNull(0)
                    val topAlbum = topAlbums?.getOrNull(0)
                    val topTrack = topTracks?.tracks?.getOrNull(0)
                    ChartComponentBox(
                        leadImage = topArtist?.bestImageUrl,
                        trailDetail = Icons.Rounded.Star,
                        shape = CircleShape,
                        artist = topArtist?.name,
                        scrobbleCount = topArtist?.playCount,
                        top = ResourceEntity.Artist,
                        album = null,
                        innerData = topArtists?.drop(1)?.fold(mutableListOf()) { list, artist ->
                            list.add(ChartData(artist.name, artist.bestImageUrl, artist.playCount))
                            list
                        }
                    )
                    Spacer(modifier = Modifier.height(70.dp))
                    ChartComponentBox(
                        leadImage = topAlbums?.getOrNull(0)?.bestImageUrl,
                        trailDetail = Icons.Outlined.Album,
                        shape = RoundedCornerShape(6.dp),
                        artist = topAlbum?.name,
                        scrobbleCount = topAlbum?.playCount?.toInt() ?: 0,
                        album = null,
                        top = ResourceEntity.Album,
                        innerData = topAlbums?.drop(1)?.fold(mutableListOf()) { list, album ->
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
                        leadImage = topTracks?.tracks?.getOrNull(0)?.bestImageUrl,
                        trailDetail = Icons.Rounded.MusicNote,
                        shape = RoundedCornerShape(6.dp),
                        artist = topTrack?.name,
                        scrobbleCount = topTrack?.playCount?.toInt() ?: 0,
                        album = null,
                        top = ResourceEntity.Track,
                        innerData = topTracks?.tracks?.drop(1)
                            ?.fold(mutableListOf()) { list, track ->
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
            PeriodPicker(true, period.value!!) {
                model.updatePeriod(it)
            }
        }
    }
}

@Composable
private fun CollageFab() {
    val nav = LocalNavigation.current
    Box(modifier = Modifier.padding(bottom = 35.dp)) {
        FloatingActionButton(
            onClick = { nav?.navigate(Routes.collage()) },
            containerColor = MostlyRed
        ) {
            Icon(Icons.Filled.AutoAwesomeMosaic, null)
        }
    }
}


@Composable
fun ChartComponentBox(
    leadImage: String?,
    trailDetail: ImageVector,
    shape: Shape,
    artist: String?,
    scrobbleCount: Int?,
    album: String?,
    innerData: List<ChartData>?,
    top: ResourceEntity
) {
    val vibrant = remember { mutableStateOf(Color.Gray) }
    val vibrantState = animateColorAsState(targetValue = vibrant.value, label = "vibrant")
    val ctx = LocalContext.current
    val nav = LocalNavigation.current

    LaunchedEffect(key1 = Unit) {
        val bmp = getBitmap(leadImage, ctx)
        val palette = createPalette(bmp)
        if (palette.vibrantSwatch == null) {
            vibrant.value = Color(palette.getDominantColor(Color.Gray.toArgb()))
        } else {
            vibrant.value = Color(palette.getVibrantColor(Color.Gray.toArgb()))
        }
    }

    val gradient = getDarkenGradient(vibrantState.value).asReversed()
    val navRoute = when (top) {
        ResourceEntity.Track -> Routes.chartsDetail(2)
        ResourceEntity.Album -> Routes.chartsDetail(1)
        ResourceEntity.Artist -> Routes.chartsDetail(0)
    }

    Row(
        modifier = Modifier.padding(start = 20.dp, end = 3.dp),
        verticalAlignment = CenterVertically,
    ) {
        Icon(trailDetail, null)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "Top $top", style = Typography.headlineSmall)
        Spacer(modifier = Modifier.weight(1f, true))
        IconButton(onClick = { nav?.navigate(navRoute) }) {
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
                Text(text = artist ?: "unknown", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                                .size(24.dp)
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

data class ChartData(
    val name: String,
    val image: String,
    val count: Int
)
