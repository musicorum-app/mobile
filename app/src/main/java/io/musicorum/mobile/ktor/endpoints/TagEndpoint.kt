package io.musicorum.mobile.ktor.endpoints

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.Image
import io.musicorum.mobile.serialization.TagData
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.Track
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

object TagEndpoint {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * @param tag The tag name
     * @param locale The language to return the wiki in, expressed as an ISO 639 alpha-2 code.
     */
    suspend fun getInfo(tag: String, locale: String? = null): TagData? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "tag.getInfo")
            parameter("tag", tag)
            parameter("lang", locale)
        }

        return if (res.status.isSuccess()) {
            val bodyAsObject = res.body<JsonObject>()
            val parsed = bodyAsObject["tag"]?.jsonObject
            if (parsed != null) {
                json.decodeFromJsonElement(parsed)
            } else null
        } else null
    }

    suspend fun getTopAlbums(tag: String, limit: Int? = null, page: Int? = null): List<TagAlbum> {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "tag.gettopalbums")
            parameter("tag", tag)
            parameter("limit", limit)
            parameter("page", page)
        }

        return if (res.status.isSuccess()) {
            val resAsObject = res.body<JsonObject>()
            val albumList = resAsObject["albums"]?.jsonObject?.get("album")?.jsonArray

            if (albumList != null) {
                json.decodeFromJsonElement(ListSerializer(TagAlbum.serializer()), albumList)
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    suspend fun getTopArtists(tag: String, limit: Int? = null, page: Int? = null): List<Artist> {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "tag.gettopartists")
            parameter("tag", tag)
            parameter("limit", limit)
            parameter("page", page)
        }

        return if (res.status.isSuccess()) {
            val resAsObject = res.body<JsonObject>()
            val artistList = resAsObject["topartists"]?.jsonObject?.get("artist")?.jsonArray

            if (artistList != null) {
                json.decodeFromJsonElement(ListSerializer(Artist.serializer()), artistList)
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    suspend fun getTopTracks(tag: String, limit: Int? = null, page: Int? = null): List<Track> {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "tag.gettoptracks")
            parameter("tag", tag)
            parameter("limit", limit)
            parameter("page", page)
        }

        return if (res.status.isSuccess()) {
            val resAsObject = res.body<JsonObject>()
            val artistList = resAsObject["tracks"]?.jsonObject?.get("track")?.jsonArray

            if (artistList != null) {
                json.decodeFromJsonElement(ListSerializer(Track.serializer()), artistList)
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}

@Serializable
data class TagAlbum(
    val name: String,
    val url: String,
    val artist: Artist,
    @SerialName("image")
    val images: List<Image>
)
