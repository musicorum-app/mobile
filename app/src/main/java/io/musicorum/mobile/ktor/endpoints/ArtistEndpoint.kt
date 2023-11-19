package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.SearchResponse
import io.musicorum.mobile.serialization.TopAlbumsResponse
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.TopTracks
import kotlinx.serialization.Serializable

object ArtistEndpoint {
    suspend fun getInfo(artist: String, username: String?): InnerArtist? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "artist.getInfo")
            parameter("name", artist)
            parameter("artist", artist)
            parameter("usernameArg", username)
        }
        return if (res.status.isSuccess()) {
            return res.body<InnerArtist>()
        } else {
            null
        }
    }

    suspend fun getTopAlbums(artist: String, limit: Int? = null): TopAlbumsResponse? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "artist.getTopAlbums")
            parameter("name", artist)
            parameter("artist", artist)
            parameter("limit", limit)
        }
        return if (res.status.isSuccess()) {
            res.body<TopAlbumsResponse>()
        } else {
            return null
        }
    }

    suspend fun getTopTracks(artist: String, limit: Int? = null): TopTracks? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "artist.getTopTracks")
            parameter("name", artist)
            parameter("artist", artist)
            parameter("limit", limit)
        }
        return if (res.status.isSuccess()) {
            res.body<TopTracks>()
        } else {
            return null
        }
    }

    suspend fun search(query: String, limit: Int? = null, page: Int? = null): SearchResponse? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "artist.search")
            parameter("artist", query)
            parameter("limit", limit)
            parameter("page", page)
        }

       return if (res.status.isSuccess()) {
            res.body<SearchResponse>()
        } else null
    }
}

@Serializable
data class InnerArtist(
    val artist: Artist
)
