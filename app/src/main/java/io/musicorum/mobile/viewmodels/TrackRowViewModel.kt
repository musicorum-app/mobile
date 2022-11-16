package io.musicorum.mobile.viewmodels

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.dataStore
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.serialization.Track
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TrackRowViewModel : ViewModel() {
    fun updateFavoritePreference(track: Track, ctx: Context) {
        viewModelScope.launch {
            TrackEndpoint().updateFavoritePreference(track, ctx)
        }
    }
}