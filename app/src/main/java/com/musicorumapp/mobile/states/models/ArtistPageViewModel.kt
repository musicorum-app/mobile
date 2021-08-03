package com.musicorumapp.mobile.states.models

import androidx.lifecycle.ViewModel
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.User
import com.musicorumapp.mobile.states.LocalAuth
import com.musicorumapp.mobile.states.LocalAuthContent

class ArtistPageViewModel(
    val artist: Artist,
    private val user: User
): ViewModel() {

}