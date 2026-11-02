package io.musicorum.mobile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.AlbumEndpoint
import io.musicorum.mobile.ktor.endpoints.ArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.TrackEndpoint
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumAlbumEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.serialization.SearchTrack
import io.musicorum.mobile.serialization.User
import io.musicorum.mobile.serialization.entities.Album
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.musicorum.TrackResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class DiscoverVm : ViewModel() {
    private val json = Json { ignoreUnknownKeys = true }
    val query = MutableLiveData("")
    val busy = MutableLiveData(false)
    val trackResults = MutableLiveData<List<SearchTrack>>(emptyList())
    val albumResults = MutableLiveData<List<Album>>(emptyList())
    val artistResults = MutableLiveData<List<Artist>>(emptyList())
    val userResult = MutableLiveData<List<User>>(emptyList())

    fun updateQuery(value: String) {
        query.value = value
    }

    fun search() {
        if (query.value!!.isEmpty()) return
        busy.value = true
        viewModelScope.launch {
            awaitAll(
                async {
                    val res = TrackEndpoint.search(query.value!!)
                    res?.let {
                        val decoded =
                            res.results["trackmatches"]?.jsonObject?.get("track")
                        decoded?.let {
                            val list = json.decodeFromJsonElement(
                                ListSerializer(SearchTrack.serializer()),
                                it
                            )
                            val musRes = MusicorumTrackEndpoint.fetchTracks(list)
                            list.forEachIndexed { index, track ->
                                musRes.bestImageUrlAt(index)?.let { imageUrl ->
                                    track.images.firstOrNull()?.url = imageUrl
                                }
                            }
                            trackResults.value = list
                        }
                    }
                },

                async {
                    val res = AlbumEndpoint.search(query.value!!)
                    res?.let {
                        val decoded = res.results["albummatches"]?.jsonObject?.get("album")
                        decoded?.let {
                            val list = json.decodeFromJsonElement(
                                ListSerializer(Album.serializer()),
                                decoded
                            )
                            val musRes = MusicorumAlbumEndpoint.fetchAlbums(list)
                            list.forEachIndexed { index, album ->
                                musRes.bestImageUrlAt(index)?.let { imageUrl ->
                                    album.bestImageUrl = imageUrl
                                }
                            }
                            albumResults.value = list
                        }
                    }
                },

                async {
                    val res = ArtistEndpoint.search(query.value!!)
                    res?.let {
                        val decoded = res.results["artistmatches"]?.jsonObject?.get("artist")
                        decoded?.let {
                            val list = json.decodeFromJsonElement(
                                ListSerializer(Artist.serializer()),
                                decoded
                            )
                            val musRes = MusicorumArtistEndpoint.fetchArtist(list)
                            list.forEachIndexed { index, artist ->
                                musRes.bestImageUrlAt(index)?.let { imageUrl ->
                                    artist.bestImageUrl = imageUrl
                                }
                            }
                            artistResults.value = list
                        }
                    }
                },

                async {
                    val res = UserEndpoint.getUser(query.value!!)
                    res?.let {
                        userResult.value = listOf(it)
                    }
                }
            )
            busy.value = false
        }
    }

}

internal fun List<TrackResponse?>.bestImageUrlAt(index: Int): String? {
    return getOrNull(index)?.bestResource?.bestImageUrl
}
