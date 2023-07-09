package io.musicorum.mobile.ktor.endpoints.musicorum

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.entities.Album
import io.musicorum.mobile.serialization.musicorum.TrackResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable

object MusicorumAlbumEndpoint {
    suspend fun fetchAlbums(albums: List<Album?>): List<TrackResponse?>? {
        if (albums.isEmpty()) return null
        val albumList: MutableList<RequestAlbum> = mutableListOf()
        albums.forEach { album ->
            album?.let {
                albumList.add(RequestAlbum(album.name.replace("- Single", ""), album.artist!!))
            }
        }
        val res = KtorConfiguration.musicorumClient.post {
            url("/v2/resources/albums")
            contentType(ContentType.Application.Json)
            setBody(Body(albumList))
        }
        return if (res.status.isSuccess()) {
            json.decodeFromString(
                ListSerializer(TrackResponse.serializer().nullable),
                res.bodyAsText()
            )
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
