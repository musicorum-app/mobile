package io.musicorum.mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import io.musicorum.mobile.LocalUser
import io.musicorum.mobile.coil.defaultImageRequestBuilder
import io.musicorum.mobile.components.CenteredLoadingSpinner
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ui.theme.EvenLighterGray
import io.musicorum.mobile.ui.theme.MostlyRed
import io.musicorum.mobile.ui.theme.Typography
import io.musicorum.mobile.utils.PeriodResolver
import io.musicorum.mobile.utils.Placeholders
import io.musicorum.mobile.utils.getDarkenGradient
import io.musicorum.mobile.viewmodels.ChartsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Charts(model: ChartsViewModel = viewModel()) {
    val period = remember { mutableStateOf("month") }
    val user = LocalUser.current ?: return
    val ctx = LocalContext.current
    val userColor = model.preferredColor.observeAsState().value
    val topArtists = model.topArtists.observeAsState().value

    LaunchedEffect(key1 = Unit) {
        if (userColor == null) model.getColor(user.user.bestImageUrl, ctx)
        if (topArtists == null) model.getTopArtists(user.user.name, FetchPeriod.WEEK)
    }

    if (userColor == null || topArtists == null) return CenteredLoadingSpinner()
    val userGradient = getDarkenGradient(userColor)

    Scaffold(floatingActionButton = { CollageFab() }) {
        Column(modifier = Modifier.padding(it)) {
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
                    ) {
                        Text(
                            text = PeriodResolver.resolve(period.value),
                            modifier = Modifier.padding(vertical = 3.dp, horizontal = 7.dp)
                        )
                    }
                }
                FilledIconButton(onClick = { /*TODO*/ }) {
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
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                        .height(70.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(text = "1", style = Typography.headlineLarge)
                        Text(text = "scrobbles", style = Typography.titleMedium)
                    }
                }
            }

            val topArtist = topArtists[0]
            ChartComponentBox(
                leadImage = topArtist.bestImageUrl,
                trailDetail = null,
                shape = CircleShape,
                artist = topArtist.name,
                scrobbleCount = topArtist.playCount,
                album = null,
                innerData = null
            )
        }
    }
}

@Composable
fun CollageFab() {
    FloatingActionButton(onClick = { /*TODO*/ }, containerColor = MostlyRed) {
        Icon(Icons.Filled.AutoAwesomeMosaic, null)
    }
}

@Composable
fun ChartComponentBox(
    leadImage: String,
    trailDetail: Any?,
    shape: Shape,
    artist: String,
    scrobbleCount: Int,
    album: String?,
    innerData: List<ChartData>?
) {
    Box(modifier = Modifier.padding(15.dp)) {
        Row {
            AsyncImage(
                model = defaultImageRequestBuilder(url = leadImage),
                contentDescription = null,
                placeholder = Placeholders.ARTIST.asPainter(),
                modifier = Modifier.clip(shape)
            )
            Column {
                Text(text = artist, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                album?.let {
                    Text(text = it, style = Typography.labelMedium)
                }
                Text("$scrobbleCount scrobbles", style = Typography.labelSmall)
            }
        }
    }
}

data class ChartData(
    val image: String,
    val name: String,
    val scrobbleCount: String
)
