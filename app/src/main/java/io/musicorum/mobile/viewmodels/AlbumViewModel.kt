package io.musicorum.mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.musicorum.mobile.ktor.endpoints.AlbumEndpoint
import io.musicorum.mobile.ktor.endpoints.InnerAlbum
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.repositories.AlbumDetailsCache
import io.musicorum.mobile.repositories.AlbumDetailsCacheEntry
import io.musicorum.mobile.repositories.AlbumDetailsCacheKey
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.musicorum.bestAvailableImageUrl
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    application: Application,
    private val detailsCache: AlbumDetailsCache
) : AndroidViewModel(application) {
    val album by lazy { MutableLiveData<InnerAlbum>() }
    val artistImage by lazy { MutableLiveData<String>() }
    val errored by lazy { MutableLiveData(false) }

    private val ctx = application
    private var currentCacheKey: AlbumDetailsCacheKey? = null
    private var loadJob: Job? = null

    fun loadAlbum(albumName: String, artistName: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            try {
                val user = LocalUserRepository(ctx).getUser()
                val cacheKey = AlbumDetailsCacheKey.create(user.username, artistName, albumName)
                currentCacheKey = cacheKey
                val cached = detailsCache.get(cacheKey)

                if (cached == null) {
                    album.value = null
                    artistImage.value = null
                    errored.value = false
                } else {
                    publishCached(cached)
                }

                refreshDetails(cacheKey, albumName, artistName, user.username, cached)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                if (album.value == null) {
                    errored.value = true
                }
            }
        }
    }

    private suspend fun refreshDetails(
        cacheKey: AlbumDetailsCacheKey,
        albumName: String,
        artistName: String,
        username: String,
        cached: AlbumDetailsCacheEntry?
    ) = coroutineScope {
        val albumRequest = if (cached == null || !detailsCache.isAlbumFresh(cached)) {
            async {
                optionalRequest { AlbumEndpoint.getInfo(albumName, artistName, username) }
            }
        } else {
            null
        }
        val artistImageRequest = if (
            cached == null || !detailsCache.isArtistArtworkFresh(cached)
        ) {
            async {
                optionalRequest {
                    MusicorumArtistEndpoint.fetchArtist(listOf(Artist(artistName)))
                        .firstOrNull()
                        ?.bestAvailableImageUrl()
                }
            }
        } else {
            null
        }

        val refreshedAlbum = albumRequest?.await()
        if (currentCacheKey == cacheKey && refreshedAlbum != null) {
            detailsCache.putAlbum(cacheKey, refreshedAlbum)
            album.value = refreshedAlbum
            errored.value = false
        } else if (currentCacheKey == cacheKey && cached == null && albumRequest != null) {
            errored.value = true
        }

        val refreshedArtistImage = artistImageRequest?.await()
        if (currentCacheKey == cacheKey && refreshedArtistImage != null) {
            detailsCache.putArtistImage(cacheKey, refreshedArtistImage)
            artistImage.value = refreshedArtistImage
        }
    }

    private fun publishCached(cached: AlbumDetailsCacheEntry) {
        album.value = cached.album
        artistImage.value = cached.artistImageUrl
        errored.value = false
    }

}
