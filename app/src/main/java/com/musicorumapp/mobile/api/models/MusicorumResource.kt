package com.musicorumapp.mobile.api.models

import com.musicorumapp.mobile.api.AlbumsResourcesRequest
import com.musicorumapp.mobile.api.ArtistsResourcesRequest
import com.musicorumapp.mobile.api.MusicorumApi
import com.musicorumapp.mobile.api.TracksResourcesRequest

class MusicorumResource {
    companion object {
        suspend fun fetchArtistsResources(artists: List<Artist>) {
            val names = artists.map { it.name }

            val resources = MusicorumApi.getResourcesEndpoint().fetchArtistsResources(
                ArtistsResourcesRequest(names)
            )

            artists.forEachIndexed { i, artist ->
                artist.resource = resources[i]
            }
        }

        suspend fun fetchAlbumsResources(albums: List<Album>) {
            val resources = MusicorumApi.getResourcesEndpoint().fetchAlbumsResources(
                AlbumsResourcesRequest(albums.map { AlbumsResourcesRequest.AlbumItem(it.name, it.artist.orEmpty()) })
            )

            albums.forEachIndexed { i, album ->
                album.resource = resources[i]
            }
        }

        suspend fun fetchTracksResources(tracks: List<Track>, deezer: Boolean = false, preview: Boolean = false, analysis: Boolean = false) {
            val resources = MusicorumApi.getResourcesEndpoint().fetchTracksResources(
                deezer = deezer,
                preview = preview,
                analysis = analysis,
                body = TracksResourcesRequest(
                    tracks = tracks.map { TracksResourcesRequest.TrackItem(it.name, it.artist.orEmpty()) }
                )
            )

            tracks.forEachIndexed { i, album ->
                album.resource = resources[i]
            }
        }
    }
}

data class ArtistResource(
    val hash: String,
    val name: String?,
    val spotify: String?,
    val image: String?
)

data class AlbumResource(
    val hash: String,
    val name: String?,
    val spotify: String?,
    val cover: String?
)

data class TrackResource(
    val hash: String,
    val name: String?,
    val artist: String?,
    val album: String?,
    val preview: String?,
    val spotify: String?,
    val deezer: String?,
    val cover: String?,
    val duration: Long?,
    val analysis: TrackResourceAnalysis?
) {
    data class TrackResourceAnalysis(
        val energy: Double?,
        val danceability: Double,
        val speechiness: Double,
        val instrumentalness: Double,
        val valence: Double,
        val tempo: Double,
    )
}