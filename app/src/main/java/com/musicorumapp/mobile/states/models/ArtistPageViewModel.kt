package com.musicorumapp.mobile.states.models

import androidx.compose.material.SnackbarHostState
import androidx.lifecycle.*
import com.musicorumapp.mobile.api.models.*
import com.musicorumapp.mobile.repos.ArtistRepository
import com.musicorumapp.mobile.states.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistPageViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val artistRepository: ArtistRepository,
    private val sessionState: SessionState
) : ViewModel() {

    private val _artist = MutableLiveData<Artist?>(null)
    private val _topTracks = MutableLiveData<PagingController<Track>?>(null)

    val artist: LiveData<Artist?> = _artist
    val topTracks: LiveData<PagingController<Track>?> = _topTracks

    fun start(snackbarHostState: SnackbarHostState?, __artist: Artist) {
        viewModelScope.launch {
            try {
                val user = sessionState.currentUser
                    ?: throw Exception("User not defined in session")

                artistRepository.getArtistInfo(__artist, user.userName)
                _artist.value = __artist

                if (__artist.imageURL == null) {
                    MusicorumResource.fetchArtistsResources(listOf(__artist))
                }

                _topTracks.value = artistRepository.getArtistTopTracks(__artist.name)

                MusicorumResource.fetchTracksResources(_topTracks.value!!.getAllItems())

            } catch (e: Exception) {
                println(e)
                snackbarHostState?.showSnackbar(e.toString())
            }
        }
    }
}