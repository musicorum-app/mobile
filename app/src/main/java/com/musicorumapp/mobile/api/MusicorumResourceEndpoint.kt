package com.musicorumapp.mobile.api

import com.musicorumapp.mobile.api.models.AlbumResource
import com.musicorumapp.mobile.api.models.ArtistResource
import com.musicorumapp.mobile.api.models.TrackResource
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface MusicorumResourceEndpoint {
    @POST("/find/artists")
    suspend fun fetchArtistsResources(
        @Body body: ArtistsResourcesRequest
    ): List<ArtistResource?>

    @POST("/find/albums")
    suspend fun fetchAlbumsResources(
        @Body body: AlbumsResourcesRequest
    ): List<AlbumResource?>

    @POST("/find/tracks")
    suspend fun fetchTracksResources(
        @Query("deezer") deezer: Boolean = false,
        @Query("preview") preview: Boolean = false,
        @Query("analysis") analysis: Boolean = false,
        @Body body: TracksResourcesRequest
    ): List<TrackResource?>
}

data class ArtistsResourcesRequest(
    val artists: List<String>
)


data class AlbumsResourcesRequest(
    val albums: List<AlbumItem>
) {
    data class AlbumItem(
        val name: String,
        val artist: String,
    )
}

data class TracksResourcesRequest(
    val tracks: List<TrackItem>
) {
    data class TrackItem(
        val name: String,
        val artist: String,
    )
}