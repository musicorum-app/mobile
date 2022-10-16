package io.musicorum.mobile.ktor.endpoints.musicorum

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.serialization.musicorum.TrackResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class MusicorumTrackEndpoint {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun fetchTracks(tracks: List<Track>): List<TrackResponse> {
        val trackList: MutableList<BodyTrack> = mutableListOf()
        tracks.forEach { t -> trackList.add(BodyTrack(t.name, t.artist.displayName)) }
        val req: HttpResponse = KtorConfiguration.musicorumClient.post {
            url("/v2/resources/tracks")
            contentType(ContentType.Application.Json)
            setBody(Body(trackList.toList()))
        }
        return json.decodeFromString(ListSerializer(TrackResponse.serializer()), req.bodyAsText())
    }
}

@kotlinx.serialization.Serializable
private data class Body(
    val tracks: List<BodyTrack>
)

@kotlinx.serialization.Serializable
private data class BodyTrack(
    val name: String,
    val artist: String
)
