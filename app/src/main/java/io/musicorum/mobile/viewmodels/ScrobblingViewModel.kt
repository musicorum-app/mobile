package io.musicorum.mobile.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.utils.ScrobbleRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScrobblingViewModel @Inject constructor(private val scrobbleRepository: ScrobbleRepository) :
    ViewModel() {
    val recentScrobbles = scrobbleRepository.recentScrobbles
    val refreshing = mutableStateOf(false)

    fun updateScrobbles(username: String) {
        refreshing.value = true
        viewModelScope.launch {
            val res = UserEndpoint.getRecentTracks(username, null, null, null)
            if (res != null) {
                scrobbleRepository.updateData(res)
            }
            refreshing.value = false
        }
    }
}