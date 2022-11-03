package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.Artist
import kotlinx.serialization.Serializable

class ArtistEndpoint {
    suspend fun getInfo(artist: String): InnerArtist? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "artist.getInfo")
            parameter("name", artist)
            parameter("artist", artist)
        }
        return if (res.status == HttpStatusCode.OK) {
            return res.body<InnerArtist>()
        } else {
            null
        }
    }
}

@Serializable
data class InnerArtist(
    val artist: Artist
)
