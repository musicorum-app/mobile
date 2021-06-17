package com.musicorumapp.mobile.ui.navigation

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.musicorumapp.mobile.R
import com.musicorumapp.mobile.ui.theme.AppMaterialIcons


data class PageIconResolveable(
    val material: ImageVector? = null,
    val drawable: Int? = null,
)


sealed class Page(
    val name: String,
    @StringRes val titleResource: Int,
    val icon: PageIconResolveable
) {
    object Home : Page(
        "home",
        R.string.bottom_navigation_item_home,
        PageIconResolveable(material = AppMaterialIcons.Home)
    )

    object Scrobbling : Page(
        "scrobbling",
        R.string.bottom_navigation_item_scrobbling,
        PageIconResolveable(drawable = R.drawable.round_queue_music_24)
    )

    object Charts : Page(
        "charts",
        R.string.bottom_navigation_item_charts,
        PageIconResolveable(drawable = R.drawable.round_show_chart_24)
    )

    object Profile : Page(
        "profile",
        R.string.bottom_navigation_item_profile,
        PageIconResolveable(material = AppMaterialIcons.Person)
    )
}

val mainPages = listOf(
    Page.Home,
    Page.Scrobbling,
    Page.Charts,
    Page.Profile
)

val pagesWithBottomBar = listOf(
    Page.Home.name,
    Page.Scrobbling.name,
    Page.Charts.name,
    Page.Profile.name,
//    "artist/{storeId}"
)