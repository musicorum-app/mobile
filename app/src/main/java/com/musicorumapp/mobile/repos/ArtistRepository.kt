package com.musicorumapp.mobile.repos

import com.musicorumapp.mobile.api.LastfmArtistEndpoint
import com.musicorumapp.mobile.api.models.Artist
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
    private val artistEndpoint: LastfmArtistEndpoint
) {
    suspend fun getArtistInfo(artist: Artist, userName: String) {
        val source = artistEndpoint.artistGetInfo(artist.name, userName).toArtist()

        println("SOURCE: ${source.listeners}")

        artist.listeners = source.listeners
        artist.playCount = source.playCount
        artist.userPlayCount = source.userPlayCount
        artist.wiki = source.wiki
        artist.tags.clear()
        artist.tags.addAll(source.tags)
    }
}