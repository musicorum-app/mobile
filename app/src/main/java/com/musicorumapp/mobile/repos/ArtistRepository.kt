package com.musicorumapp.mobile.repos

import com.musicorumapp.mobile.api.LastfmApi
import com.musicorumapp.mobile.api.LastfmArtistEndpoint
import com.musicorumapp.mobile.api.LastfmTrackEndpoint
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.PagingController
import com.musicorumapp.mobile.api.models.Track
import com.musicorumapp.mobile.utils.Utils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
    private val artistEndpoint: LastfmArtistEndpoint,
    private val trackEndpoint: LastfmTrackEndpoint
) {
    suspend fun getArtistInfo(artist: Artist, userName: String) {
        val source = artistEndpoint.artistGetInfo(artist.name, userName).toArtist()

        artist.listeners = source.listeners
        artist.playCount = source.playCount
        artist.userPlayCount = source.userPlayCount
        artist.wiki = source.wiki

        artist.tags.clear()
        artist.tags.addAll(source.tags)

        println("Simular: ${source.similar}")
        artist.similar.clear()
        artist.similar.addAll(source.similar)
    }

    suspend fun getArtistTopTracks(artist: String, perPage: Int = 20): PagingController<Track> {
        val controller = PagingController(
            perPage = perPage,
            requester = { pg ->
                artistEndpoint.getTopTracks(artist, limit = perPage).map { it.toTrack() }
            }
        )
        controller.doRequest(1)

        return controller
    }
}