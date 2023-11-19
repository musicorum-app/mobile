package io.musicorum.mobile.ktor.endpoints

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.isSuccess
import io.musicorum.mobile.ktor.KtorConfiguration
import io.musicorum.mobile.serialization.BaseIndividualTrack
import io.musicorum.mobile.serialization.SearchResponse
import io.musicorum.mobile.serialization.SimilarTrack
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.userData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object TrackEndpoint {
    suspend fun getTrack(
        trackName: String,
        artist: String,
        username: String?,
        autoCorrect: Boolean?
    ): BaseIndividualTrack? {
        val res = KtorConfiguration.lastFmClient.get {
            val autoCorrectValue = if (autoCorrect == true) 1 else 0
            parameter("track", trackName)
            parameter("method", "track.getInfo")
            parameter("usernameArg", username)
            parameter("artist", artist)
            parameter("autocorrect", autoCorrectValue)
        }
        return if (res.status.isSuccess()) {
            res.body<BaseIndividualTrack>()
        } else {
            null
        }
    }

    suspend fun updateFavoritePreference(track: Track, ctx: Context) {
        val sessionKey = ctx.userData.data.map { prefs ->
            prefs[stringPreferencesKey("session_key")]!!
        }.first()
        KtorConfiguration.lastFmClient.post {
            parameter("method", if (track.loved) "track.unlove" else "track.love")
            parameter("track", track.name)
            parameter("artist", track.artist.name)
            parameter("sk", sessionKey)
        }
    }

    suspend fun updateFavoritePreference(track: Track, loved: Boolean, ctx: Context) {
        val sessionKey = ctx.userData.data.map { prefs ->
            prefs[stringPreferencesKey("session_key")]!!
        }.first()
        KtorConfiguration.lastFmClient.post {
            parameter("method", if (loved) "track.unlove" else "track.love")
            parameter("track", track.name)
            parameter("artist", track.artist.name)
            parameter("sk", sessionKey)
        }
    }

    suspend fun fetchSimilar(baseTrack: Track, limit: Int?, autoCorrect: Boolean?): SimilarTrack? {
        val autoCorrectValue = if (autoCorrect == true) "1" else "0"
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "track.getSimilar")
            parameter("artist", baseTrack.artist.name)
            parameter("track", baseTrack.name)
            parameter("limit", limit)
            parameter("autocorrect", autoCorrectValue)
        }
        return if (res.status.isSuccess()) {
            res.body<SimilarTrack>()
        } else {
            null
        }
    }

    suspend fun search(query: String, limit: Int? = null, page: Int? = null, artist: String? = null): SearchResponse? {
        val res = KtorConfiguration.lastFmClient.get {
            parameter("method", "track.search")
            parameter("track", query)
            parameter("artist", artist)
            parameter("limit", limit)
            parameter("page", page)
        }

        return if (res.status.isSuccess()) {
            res.body<SearchResponse>()
        } else null
    }

}
