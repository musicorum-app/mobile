package io.musicorum.mobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.musicorum.mobile.ktor.endpoints.ArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.repositories.ArtistDetailsCache
import io.musicorum.mobile.repositories.ArtistDetailsCacheEntry
import io.musicorum.mobile.repositories.ArtistDetailsCacheKey
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.repositories.normalizedCacheValue
import io.musicorum.mobile.serialization.TopAlbum
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.InnerArtist
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.serialization.musicorum.bestAvailableImageUrl
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    application: Application,
    private val detailsCache: ArtistDetailsCache
) : AndroidViewModel(application) {
    val artist by lazy { MutableLiveData<Artist>() }
    val topTracks by lazy { MutableLiveData<List<Track>>() }
    val topAlbums by lazy { MutableLiveData<List<TopAlbum>>() }

    private val ctx = application
    private var currentCacheKey: ArtistDetailsCacheKey? = null
    private var loadJob: Job? = null

    fun loadArtist(artistName: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            try {
                val user = LocalUserRepository(ctx).getUser()
                val cacheKey = ArtistDetailsCacheKey.create(user.username, artistName)
                currentCacheKey = cacheKey
                val cached = detailsCache.get(cacheKey)

                if (cached == null) {
                    artist.value = null
                    topAlbums.value = null
                    topTracks.value = null
                } else {
                    publishCached(cached)
                }

                refreshDetails(cacheKey, artistName, user.username, cached)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                // A cached value remains visible when an optional refresh fails.
            }
        }
    }

    private suspend fun refreshDetails(
        cacheKey: ArtistDetailsCacheKey,
        artistName: String,
        username: String,
        cached: ArtistDetailsCacheEntry?
    ) = coroutineScope {
        val refreshArtist = cached == null || !detailsCache.isArtistFresh(cached)
        val shouldRefreshArtwork = cached == null || !detailsCache.isArtworkFresh(cached)
        val refreshTopAlbums = cached == null || !detailsCache.areTopAlbumsFresh(cached)
        val refreshTopTracks = cached == null || !detailsCache.areTopTracksFresh(cached)

        val artistRequest = if (refreshArtist) {
            async { optionalRequest { ArtistEndpoint.getInfo(artistName, username)?.artist } }
        } else {
            null
        }
        val topAlbumsRequest = if (refreshTopAlbums) {
            async { optionalRequest { ArtistEndpoint.getTopAlbums(artistName)?.topAlbums?.albums } }
        } else {
            null
        }
        val topTracksRequest = if (refreshTopTracks) {
            async { optionalRequest { ArtistEndpoint.getTopTracks(artistName)?.topTracks?.tracks } }
        } else {
            null
        }

        val refreshedArtist = artistRequest?.await()
        var artistToPublish = refreshedArtist
        if (refreshedArtist != null && cached != null) {
            artistToPublish = mergeArtwork(refreshedArtist, cached.artist)
        } else if (artistToPublish == null && shouldRefreshArtwork) {
            artistToPublish = cached?.artist?.copyWithArtwork()
        }

        var artworkRefreshed = false
        if (shouldRefreshArtwork && artistToPublish != null) {
            artworkRefreshed = refreshArtwork(artistToPublish)
        }

        if (currentCacheKey == cacheKey && refreshedArtist != null && artistToPublish != null) {
            detailsCache.putArtist(cacheKey, artistToPublish, artworkRefreshed)
            artist.value = artistToPublish
        } else if (currentCacheKey == cacheKey && artworkRefreshed && artistToPublish != null) {
            detailsCache.updateArtwork(cacheKey, artistToPublish)
            artist.value = artistToPublish
        }

        val refreshedAlbums = topAlbumsRequest?.await()
        if (currentCacheKey == cacheKey && refreshedAlbums != null) {
            detailsCache.putTopAlbums(cacheKey, refreshedAlbums)
            topAlbums.value = refreshedAlbums
        }

        val refreshedTracks = topTracksRequest?.await()
        if (currentCacheKey == cacheKey && refreshedTracks != null) {
            detailsCache.putTopTracks(cacheKey, refreshedTracks)
            topTracks.value = refreshedTracks
        }
    }

    private fun publishCached(cached: ArtistDetailsCacheEntry) {
        artist.value = cached.artist
        topAlbums.value = cached.topAlbums
        topTracks.value = cached.topTracks
    }

    private suspend fun refreshArtwork(value: Artist): Boolean {
        val similarArtists = value.similar?.artist.orEmpty()
        val resources = optionalRequest {
            MusicorumArtistEndpoint.fetchArtist(listOf(value) + similarArtists)
        }.orEmpty()

        val artistImage = resources.firstOrNull()?.bestAvailableImageUrl()
        if (artistImage != null) {
            value.bestImageUrl = artistImage
        }
        similarArtists.forEachIndexed { index, similarArtist ->
            resources.getOrNull(index + 1)?.bestAvailableImageUrl()?.let { imageUrl ->
                similarArtist.bestImageUrl = imageUrl
            }
        }
        return artistImage != null
    }

    private fun mergeArtwork(fresh: Artist, cached: Artist): Artist {
        fresh.bestImageUrl = cached.bestImageUrl
        val cachedSimilar = cached.similar?.artist.orEmpty().associateBy {
            it.name.normalizedCacheValue()
        }
        fresh.similar?.artist.orEmpty().forEach { similarArtist ->
            cachedSimilar[similarArtist.name.normalizedCacheValue()]?.let { cachedArtist ->
                similarArtist.bestImageUrl = cachedArtist.bestImageUrl
            }
        }
        return fresh
    }

    private fun Artist.copyWithArtwork(): Artist {
        val copiedSimilar = similar?.artist?.map { similarArtist ->
            similarArtist.copy().also { it.bestImageUrl = similarArtist.bestImageUrl }
        }
        return copy(
            similar = copiedSimilar?.let(::InnerArtist)
        ).also { it.bestImageUrl = bestImageUrl }
    }

}
