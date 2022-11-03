package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.Album
import kotlinx.serialization.Serializable

class AlbumEndpoint {
    suspend fun getInfo(album: String, artist: String): InnerAlbum? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("artist", artist)
            parameter("album", album)
            parameter("method", "album.getInfo")
        }
        return if (res.status == HttpStatusCode.OK) {
            res.body<InnerAlbum>()
        } else {
            null
        }
    }
}

@Serializable
data class InnerAlbum(
    val album: Album
)
