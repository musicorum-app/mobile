package io.musicorum.mobile.ktor.endpoints.musicorum

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.Album
import io.musicorum.mobile.serialization.musicorum.TrackResponse
import kotlinx.serialization.builtins.ListSerializer

class MusicorumAlbumEndpoint {
    suspend fun fetchAlbums(albums: List<Album>): List<TrackResponse>? {
        val albumList: MutableList<RequestAlbum> = mutableListOf()
        albums.forEach { album ->
            albumList.add(RequestAlbum(album.name.replace("- Single", ""), album.artist!!))
        }
        val res = KtorConfiguration.musicorumClient.post {
            url("/v2/resources/albums")
            contentType(ContentType.Application.Json)
            setBody(Body(albumList))
        }
        return if (res.status.isSuccess()) {
            json.decodeFromString(ListSerializer(TrackResponse.serializer()), res.bodyAsText())
        } else null
    }

    @kotlinx.serialization.Serializable
    private data class Body(
        val albums: List<RequestAlbum>
    )

    @kotlinx.serialization.Serializable
    private data class RequestAlbum(
        val name: String,
        val artist: String
    )
}
