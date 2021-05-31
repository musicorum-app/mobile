package com.musicorumapp.mobile.states.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.api.LastfmApi
import com.musicorumapp.mobile.api.models.Album
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.Track
import com.musicorumapp.mobile.api.models.User
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class DiscoverPageViewModel(
    private val authenticationViewModel: AuthenticationViewModel
) : ViewModel() {

    private val _searchState = MutableLiveData(SearchState.NONE)

    private val _results = MutableLiveData(SearchResults())

    val searchState: LiveData<SearchState> = _searchState
    val results: LiveData<SearchResults> = _results

    fun search (query: String) {
        viewModelScope.launch {
            try {
                awaitAll(
                    async {
                        val controller = LastfmApi.searchArtists(query, 20)

                        Log.i(Constants.LOG_TAG, controller.toString())
                        Log.i(Constants.LOG_TAG, controller.getAllItems()[0].toString())
                    },
                    async {}
                )
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "Discover search error: $e")
            }
        }
    }


    enum class SearchState {
        NONE, LOADING, RESULTS, ERROR
    }
}

data class SearchResults(
    val artists: List<Artist> = emptyList(),
    val tracks: List<Track> = emptyList(),
    val albums: List<Album> = emptyList(),
    val user: User? = null
)