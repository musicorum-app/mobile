package io.musicorum.mobile.views.charts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.models.ResourceEntity
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.serialization.TopAlbum
import io.musicorum.mobile.serialization.entities.TopArtist
import io.musicorum.mobile.serialization.entities.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    val artists = MutableLiveData<List<TopArtist>>()
    val albums = MutableLiveData<List<TopAlbum>>()
    val tracks = MutableLiveData<List<Track>>()
    val busy = MutableLiveData(false)
    val period = MutableLiveData(FetchPeriod.WEEK)
    val entity = MutableLiveData<ResourceEntity>()
    val application_ = application
    val viewMode = MutableLiveData(ViewMode.List)

    fun updatePeriod(newPeriod: FetchPeriod) {
        period.value = newPeriod
        albums.value = emptyList()
        tracks.value = emptyList()
        artists.value = emptyList()
        viewModelScope.launch {
            fetch()
        }
    }

    init {
        viewModelScope.launch {
            fetch()
        }
    }

    fun refetch(newEntity: ResourceEntity) {
        entity.value = newEntity
        viewModelScope.launch {
            fetch()
        }
    }

    private suspend fun fetch() {
        if (entity.value == null) return
        val user = LocalUserRepository(application_.applicationContext).partialUser.first()
        val entity = entity.value
        val period = period.value!!
        if (entity == ResourceEntity.Artist) {
            if (artists.value?.isNotEmpty() == true) return
            busy.value = true
            val res = UserEndpoint.getTopArtists(user.username, null, period)
            val artistsRes = res?.topArtists?.artists
            if (artistsRes == null) {
                busy.value = false
                return
            }
            val musRes = MusicorumArtistEndpoint.fetchArtist(artistsRes)
            artistsRes.onEachIndexed { i, a ->
                a.bestImageUrl = musRes[i].bestResource?.bestImageUrl!!
            }
            artists.value = artistsRes
            busy.value = false
        }

        if (entity == ResourceEntity.Album) {
            if (albums.value?.isNotEmpty() == true) return
            busy.value = true
            val res = UserEndpoint.getTopAlbums(user.username, period, null)
            albums.value = res?.topAlbums?.albums
            busy.value = false
        }

        if (entity == ResourceEntity.Track) {
            if (tracks.value?.isNotEmpty() == true) return
            busy.value = true
            val res = UserEndpoint.getTopTracks(user.username, period, null)
            res?.topTracks?.tracks?.let { MusicorumTrackEndpoint.fetchTracks(it) }
                ?.forEachIndexed { index, trackResponse ->
                    res.topTracks.tracks[index].bestImageUrl =
                        trackResponse?.bestResource?.bestImageUrl.toString()
                }
            tracks.value = res?.topTracks?.tracks
            busy.value = false
        }
    }
}