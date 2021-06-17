package com.musicorumapp.mobile.states

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.musicorumapp.mobile.api.models.Album
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.Track
import com.musicorumapp.mobile.api.models.User
import com.musicorumapp.mobile.utils.Utils

class LocalNavigationContextContent(
    val navigationController: NavHostController? = null,
    val artistsStore: MutableMap<String, Artist> = mutableMapOf(),
    val albumsStore: MutableMap<String, Album> = mutableMapOf(),
    val tracksStore: MutableMap<String, Track> = mutableMapOf(),
    val usersStore: MutableMap<String, User> = mutableMapOf(),
) {
    fun addArtist(artist: Artist): String {
        val id = Utils.md5Hash(artist.name)
        artistsStore[id] = artist
        return id
    }
}

val LocalNavigationContext = compositionLocalOf {
    LocalNavigationContextContent()
}