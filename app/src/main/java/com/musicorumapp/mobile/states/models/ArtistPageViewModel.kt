package com.musicorumapp.mobile.states.models

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.*
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.musicorumapp.mobile.api.models.*
import com.musicorumapp.mobile.repos.ArtistRepository
import com.musicorumapp.mobile.states.SessionState
import com.musicorumapp.mobile.utils.PredominantColorState
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
    private val _fetched = MutableLiveData(false)
    private val _topTracks = MutableLiveData<PagingController<Track>?>(null)
    private val _topAlbums = MutableLiveData<PagingController<Album>?>(null)
    private val _predominantColor = MutableLiveData<Color?>(null)
    private val _imageBitmap = MutableLiveData<Bitmap?>(null)

    val fetched: LiveData<Boolean> = _fetched
    val artist: LiveData<Artist?> = _artist
    val topTracks: LiveData<PagingController<Track>?> = _topTracks
    val topAlbums: LiveData<PagingController<Album>?> = _topAlbums
    val predominantColor: LiveData<Color?> = _predominantColor
    val imageBitmap: LiveData<Bitmap?> = _imageBitmap

    fun start(
        context: Context,
        predominantColorState: PredominantColorState,
        snackbarHostState: SnackbarHostState?,
        __artist: Artist
    ) {
        viewModelScope.launch {
            try {
                val user = sessionState.currentUser
                    ?: throw Exception("User not defined in session")

                artistRepository.getArtistInfo(__artist, user.userName)
                _artist.value = __artist


                if (__artist.imageURL == null) {
                    __artist.onResourcesChange {
                        fetchColors(context, predominantColorState, snackbarHostState)
                    }
                    MusicorumResource.fetchArtistsResources(listOf(__artist))
                } else {
                    fetchColors(context, predominantColorState, snackbarHostState)
                }

                MusicorumResource.fetchArtistsResources(__artist.similar)

                _topTracks.value = artistRepository.getArtistTopTracks(__artist.name)
                _topAlbums.value = artistRepository.getArtistTopAlbums(__artist.name)

                MusicorumResource.fetchTracksResources(_topTracks.value!!.getAllItems())

                _fetched.value = true

            } catch (e: Exception) {
                println(e)
                snackbarHostState?.showSnackbar(e.toString())
            }
        }
    }

    private fun fetchColors(
        context: Context,
        predominantColorState: PredominantColorState,
        snackbarHostState: SnackbarHostState?
    ) {
        viewModelScope.launch {
            val req = ImageRequest.Builder(context)
                .data(_artist.value?.imageURL)
                .allowHardware(false)
                .build()

            val result = Coil.execute(req)

            if (result is SuccessResult) {
                val bitmap = result.drawable.toBitmap()
                _imageBitmap.value = bitmap
                predominantColorState.resolveColorsFromBitmap(bitmap)
                _predominantColor.value = predominantColorState.color
            } else {
                println(result)
                snackbarHostState?.showSnackbar("Could not download image")
            }
        }
    }
}