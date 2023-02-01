package io.musicorum.mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.musicorum.mobile.repositories.RecentTracksRepository
import io.musicorum.mobile.serialization.Track
import kotlinx.coroutines.flow.Flow

class RecentSrcobblesViewModel : ViewModel() {
    fun fetchRecentTracks(username: String): Flow<PagingData<Track>> =
        RecentTracksRepository.getRecentTracks(username).cachedIn(viewModelScope)
}