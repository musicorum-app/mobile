package com.musicorumapp.mobile.states.models

import androidx.compose.material.SnackbarHostState
import androidx.lifecycle.*
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.MusicorumResource
import com.musicorumapp.mobile.api.models.User
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

    val artist: LiveData<Artist?> = _artist

    fun start(snackbarHostState: SnackbarHostState?, __artist: Artist) {
        println(__artist)
        viewModelScope.launch {
            try {

                println("VIEWMODEL TEST: $artistRepository")
                val user =
                    sessionState.currentUser ?: throw Exception("User not defined in session")

                artistRepository.getArtistInfo(__artist, user.userName)
                _artist.value = __artist

                println(__artist.listeners)

                if (__artist.imageURL == null) {
                    MusicorumResource.fetchArtistsResources(listOf(__artist))
                }

                println(artist.value?.listeners)
            } catch (e: Exception) {
                println(e)
                snackbarHostState?.showSnackbar(e.toString())
            }
        }
    }
}