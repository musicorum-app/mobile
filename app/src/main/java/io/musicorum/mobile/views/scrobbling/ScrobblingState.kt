package io.musicorum.mobile.views.scrobbling

import android.graphics.drawable.Drawable
import io.musicorum.mobile.serialization.entities.Track

data class ScrobblingState(
    val playingTrack: Track? = null,
    val isRefreshing: Boolean = false,
    val isTrackLoved: Boolean = false,
    val scrobblingAppName: String = "",
    val scrobblingAppIcon: Drawable? = null,
    val recentScrobbles: List<Track>? = null
)
