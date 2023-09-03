package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.SearchResponse
import io.musicorum.mobile.serialization.entities.Album
import kotlinx.serialization.Serializable

object AlbumEndpoint {
    suspend fun getInfo(album: String, artist: String, user: String?): InnerAlbum? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("artist", artist)
            parameter("album", album)
            parameter("method", "album.getInfo")
            parameter("user", user)
        }
        return if (res.status.isSuccess()) {
            res.body<InnerAlbum>()
        } else {
            null
        }
    }

    suspend fun search(query: String, limit: Int? = null, page: Int? = null): SearchResponse? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "album.search")
            parameter("album", query)
            parameter("limit", limit)
            parameter("page", page)
        }

        return if (res.status.isSuccess()) {
            res.body<SearchResponse>()
        } else null
    }
}

@Serializable
data class InnerAlbum(
    val album: Album
)
