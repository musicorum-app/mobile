package io.musicorum.mobile.ktor.endpoints.musicorum

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.Artist
import io.musicorum.mobile.serialization.musicorum.TrackResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

class MusicorumArtistEndpoint {
    suspend fun fetchArtist(artists: List<Artist>): List<TrackResponse> {
        val artistsList = mutableListOf<String>()
        artists.forEach { artist -> artistsList.add(artist.name) }
        val body = ArtistBody(artistsList)
        val res = KtorConfiguration.musicorumClient.post {
            url("/v2/resources/artists")
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return json.decodeFromString(ListSerializer(TrackResponse.serializer()), res.bodyAsText())
    }
}

@kotlinx.serialization.Serializable
private data class ArtistBody(
    val artists: List<String>
)
