package com.musicorumapp.mobile.states.models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicorumapp.mobile.repos.ArtistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistPageViewModel @Inject constructor(
//    private val savedStateHandle: SavedStateHandle,
    private val artistRepository: ArtistRepository
): ViewModel() {
    init {
        viewModelScope.launch {
            println("VIEWMODEL TEST: $artistRepository")
//            artistRepository.getArtistInfo(artist, user.userName)
        }
    }
}