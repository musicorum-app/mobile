package io.musicorum.mobile.ktor.endpoints.musicorum

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.TopArtist
import io.musicorum.mobile.serialization.musicorum.TrackResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

internal val json = Json {
    ignoreUnknownKeys = true
}

object MusicorumArtistEndpoint {
    suspend fun fetchArtist(artists: List<Artist>): List<TrackResponse> {
        if (artists.isEmpty()) return emptyList()
        val artistsList = mutableListOf<String>()
        artists.forEach { artist -> artistsList.add(artist.name) }
        val body = Body(artistsList)
        val res = KtorConfiguration.musicorumClient.post {
            url("/v2/resources/artists")
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return if (res.status.isSuccess()) {
            json.decodeFromString(ListSerializer(TrackResponse.serializer()), res.bodyAsText())
        } else {
            emptyList()
        }
    }

    @JvmName("fetchArtist1")
    suspend fun fetchArtist(artists: List<TopArtist>): List<TrackResponse> {
        if (artists.isEmpty()) return emptyList()
        val artistsList = mutableListOf<String>()
        artists.forEach { artist -> artistsList.add(artist.name) }
        val body = Body(artistsList)
        val res = KtorConfiguration.musicorumClient.post {
            url("/v2/resources/artists")
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return if (res.status.isSuccess()) {
            json.decodeFromString(ListSerializer(TrackResponse.serializer()), res.bodyAsText())
        } else emptyList()

    }

    @kotlinx.serialization.Serializable
    private data class Body(
        val artists: List<String>
    )
}
