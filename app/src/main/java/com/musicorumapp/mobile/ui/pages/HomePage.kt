package com.musicorumapp.mobile.ui.pages

import androidx.activity.ComponentActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.User
import com.musicorumapp.mobile.authentication.AuthenticationPreferences
import com.musicorumapp.mobile.states.LocalAuth
import com.musicorumapp.mobile.states.LocalNavigationContext
import com.musicorumapp.mobile.states.models.AuthenticationViewModel
import com.musicorumapp.mobile.states.models.HomePageViewModel
import com.musicorumapp.mobile.ui.components.ArtistListItem
import com.musicorumapp.mobile.ui.components.NetworkImage
import com.musicorumapp.mobile.ui.components.PulsatingSkeleton
import com.musicorumapp.mobile.ui.components.Title
import com.musicorumapp.mobile.ui.theme.KindaBlack
import com.musicorumapp.mobile.ui.theme.PaddingSpacing
import com.musicorumapp.mobile.ui.theme.Shapes
import com.musicorumapp.mobile.utils.calculateColorContrast
import com.musicorumapp.mobile.utils.darkerColor
import com.musicorumapp.mobile.utils.gradientBackgroundColorResolver
import com.musicorumapp.mobile.utils.rememberPredominantColor

@Composable
fun HomePage(
    authenticationViewModel: AuthenticationViewModel? = null,
    homePageViewModel: HomePageViewModel = viewModel()
) {

    val prefs = LocalContext.current.getSharedPreferences(
        Constants.AUTH_PREFS_KEY,
        ComponentActivity.MODE_PRIVATE
    )
    val authPrefs = AuthenticationPreferences(prefs)

    val authContent = LocalAuth.current
    val navigationContext = LocalNavigationContext.current


    Column(
        modifier = Modifier
            .padding(horizontal = PaddingSpacing.HorizontalMainPadding)
            .padding(top = 6.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Title(text = stringResource(id = R.string.bottom_navigation_item_home), showSearch = true, showSettings = true)

        Spacer(modifier = Modifier.height(6.dp))

        UserCard(
            user = authenticationViewModel?.user?.value,
            homePageViewModel = homePageViewModel
        )

        Spacer(modifier = Modifier.height(14.dp))

        val artist = Artist.fromSample()

        ArtistListItem(artist = artist, modifier = Modifier.clickable {
            val id = navigationContext.addArtist(artist)
            navigationContext.navigationController?.navigate("artist/$id")
        })
    }
}

private val cardHeight = 140.dp

@Composable
fun UserCard(
    user: User? = null,
    homePageViewModel: HomePageViewModel
) {
    val modifier = Modifier
        .fillMaxWidth()
        .height(cardHeight)
        .clip(Shapes.medium)

    val predominantColor = homePageViewModel.predominantColor

    val predominantColorState = rememberPredominantColor(
        colorFinder = {
            it.lightVibrantSwatch ?: it.vibrantSwatch ?: it.swatches.maxByOrNull { s ->
                calculateColorContrast(
                    Color(s.rgb), KindaBlack
                )
            }
        }
    ) {
        gradientBackgroundColorResolver(it)
    }

    if (user != null && !homePageViewModel?.colorFetched?.value!!) {
        user.images.bestImage?.let {
            homePageViewModel.fetchColors(
                predominantColorState,
                url = it
            )
        }
    }

    Crossfade(targetState = user == null) {
        if (it) PulsatingSkeleton(modifier)
        else Box(
            modifier = modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            predominantColor.value ?: predominantColorState.color,
                            darkerColor(predominantColor.value ?: predominantColorState.color, 22)
                        ),
//                    start = Offset(0f, 0f),
//                    end = Offset(1f, 1f),
                    )
                )
                .padding(PaddingSpacing.MediumPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                    NetworkImage(
                        url = user?.images?.bestImage, contentDescription = stringResource(
                            R.string.home_page_image_content_user
                        ),
                        modifier = Modifier
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                            )
                    )
                Spacer(modifier = Modifier.width(PaddingSpacing.SmallPadding))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        user?.displayName.orEmpty(),
                        fontWeight = FontWeight.Bold,
                        color = predominantColorState.onColor,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp
                    )
                    Text(
                        stringResource(
                            R.string.home_page_scrobbles_text,
                            user?.playCount.toString()
                        ),
                        style = MaterialTheme.typography.body2,
                        color = predominantColorState.onColor,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}