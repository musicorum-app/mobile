package io.musicorum.mobile.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.serialization.Track
import kotlinx.coroutines.launch

class TrackRowViewModel : ViewModel() {
    fun updateFavoritePreference(track: Track, ctx: Context) {
        viewModelScope.launch {
            TrackEndpoint.updateFavoritePreference(track, ctx)
        }
    }
}