package io.musicorum.mobile.ktor.endpoints.musicorum

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.takeFrom
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.Track
import io.musicorum.mobile.serialization.musicorum.TrackResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.Json

object MusicorumTrackEndpoint {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true
    }

    suspend fun fetchTracks(tracks: List<Track>): List<TrackResponse?>? {
        val trackList: MutableList<BodyTrack> = mutableListOf()
        tracks.forEach { t -> trackList.add(BodyTrack(t.name, t.artist.name)) }
        val res: HttpResponse = KtorConfiguration.musicorumClient.post {
            url("/v2/resources/tracks")
            contentType(ContentType.Application.Json)
            setBody(Body(trackList.toList()))
        }

        /* Re-run requests that are 201 -- Experimental */
        if (res.status == HttpStatusCode.Created) {
            val requestBuilder = HttpRequestBuilder().takeFrom(res.request)
            val newRes = KtorConfiguration.musicorumClient.post(requestBuilder)
            return if (res.status.isSuccess()) {
                json.decodeFromString(
                    ListSerializer(TrackResponse.serializer().nullable),
                    newRes.bodyAsText()
                )
            } else null
        }

        return if (res.status.isSuccess()) {
            json.decodeFromString(ListSerializer(TrackResponse.serializer()), res.bodyAsText())
        } else null

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
}
