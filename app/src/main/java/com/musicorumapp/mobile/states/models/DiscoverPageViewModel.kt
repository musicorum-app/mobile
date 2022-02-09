package com.musicorumapp.mobile.states.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.api.LastfmApi
import com.musicorumapp.mobile.api.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class DiscoverPageViewModel() : ViewModel() {

    private val _searchState = MutableLiveData(SearchState.NONE)

    private val _results = MutableLiveData(SearchResults())

    private val _resourcesFetched = MutableLiveData(false)

    val searchState: LiveData<SearchState> = _searchState
    val results: LiveData<SearchResults> = _results
    val resourcesFetched: LiveData<Boolean> = _resourcesFetched

    fun search(query: String) {
        _searchState.value = SearchState.LOADING
        _resourcesFetched.value = false
        viewModelScope.launch {
            try {
                val resultsData = SearchResults(hasResults = true)

                awaitAll(
                    async {
                        try {
                            Log.i(Constants.LOG_TAG, "Searching for artists...")
                            val controller = LastfmApi.searchArtists(query, 3)

                            resultsData.artists = controller
                        } catch (e: Exception) {
                            Log.w(Constants.LOG_TAG, "Discover search error: $e")
                        }
                    },
                    async {
                        try {
                            Log.i(Constants.LOG_TAG, "Searching for albums...")
                            val controller = LastfmApi.searchAlbums(query, 3)

                            resultsData.albums = controller
                        } catch (e: Exception) {
                            Log.w(Constants.LOG_TAG, "Discover search error: $e")
                        }
                    },
                    async {
                        try {
                            Log.i(Constants.LOG_TAG, "Searching for tracks...")
                            val controller = LastfmApi.searchTracks(query, 3)

                            resultsData.tracks = controller
                        } catch (e: Exception) {
                            Log.w(Constants.LOG_TAG, "Discover search error: $e")
                        }
                    },
                    async {
                        try {
                            Log.i(Constants.LOG_TAG, "Searching for user...")
                            val userResp = LastfmApi.getUserEndpoint().getUserInfo(query)

                            resultsData.user = userResp.toUser()
                        } catch (e: Exception) {
                            Log.w(Constants.LOG_TAG, "Discover search error: $e")
                        }
                    }
                )

                _results.value = resultsData
                _searchState.value = SearchState.RESULTS

                awaitAll(
                    async {
                        try {
                            Log.i(Constants.LOG_TAG, "Fetching artists resources...")
                            if (resultsData.artists != null) {
                                MusicorumResource.fetchArtistsResources(resultsData.artists!!.getAllItems())
                            }
                            null
                        } catch (e: Exception) {
                            Log.w(Constants.LOG_TAG, "Artists resources error: $e")
                        }
                    },
                    async {
                        try {
                            Log.i(Constants.LOG_TAG, "Fetching tracks resources...")
                            if (resultsData.tracks != null) {
                                MusicorumResource.fetchTracksResources(resultsData.tracks!!.getAllItems())
                            }
                            null
                        } catch (e: Exception) {
                            Log.w(Constants.LOG_TAG, "Artists tracks error: $e")
                        }
                    },
                )

                _resourcesFetched.value = true
            } catch (e: Exception) {
                Log.w(Constants.LOG_TAG, "Discover search error: $e")
                _searchState.value = SearchState.ERROR
            }
        }
    }


    enum class SearchState {
        NONE, LOADING, RESULTS, ERROR
    }
}

data class SearchResults(
    val hasResults: Boolean = false,
    var artists: PagingController<Artist>? = null,
    var albums: PagingController<Album>? = null,
    var tracks: PagingController<Track>? = null,
    var user: User? = null
)