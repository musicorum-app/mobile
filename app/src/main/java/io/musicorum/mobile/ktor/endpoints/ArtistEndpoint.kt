package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.Artist
import io.musicorum.mobile.serialization.TopAlbumsResponse
import kotlinx.serialization.Serializable

class ArtistEndpoint {
    suspend fun getInfo(artist: String, username: String?): InnerArtist? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "artist.getInfo")
            parameter("name", artist)
            parameter("artist", artist)
            parameter("username", username)
        }
        return if (res.status.isSuccess()) {
            return res.body<InnerArtist>()
        } else {
            null
        }
    }

    suspend fun getTopAlbums(artist: String): TopAlbumsResponse? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "artist.getTopAlbums")
            parameter("name", artist)
            parameter("artist", artist)
        }
        return if (res.status.isSuccess()) {
            res.body<TopAlbumsResponse>()
        } else {
            return null
        }
    }
}

@Serializable
data class InnerArtist(
    val artist: Artist
)
