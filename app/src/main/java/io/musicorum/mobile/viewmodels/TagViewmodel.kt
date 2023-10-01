package io.musicorum.mobile.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import io.musicorum.mobile.ktor.endpoints.TagAlbum
import io.musicorum.mobile.ktor.endpoints.TagEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumAlbumEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumArtistEndpoint
import io.musicorum.mobile.ktor.endpoints.musicorum.MusicorumTrackEndpoint
import io.musicorum.mobile.serialization.TagData
import io.musicorum.mobile.serialization.entities.Artist
import io.musicorum.mobile.serialization.entities.Track
import io.musicorum.mobile.utils.getBitmap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class TagViewmodel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val tagInfo = MutableLiveData<TagData?>(null)
    val tagAlbums = MutableLiveData<List<TagAlbum>?>(null)
    val imagePalette = MutableLiveData<Palette?>(null)
    val topArtists = MutableLiveData<List<Artist>?>(null)
    val tracks = MutableLiveData<List<Track>?>(null)
    val ctx = application.applicationContext

    //private val _uiState = MutableStateFlow(TagViewState())
    //val uiState: StateFlow<TagViewState> = _uiState.asStateFlow()
    val tag = savedStateHandle.get<String>("tagName")

    private fun init() {
        if (tag == null) return
        viewModelScope.launch {
            awaitAll(
                async {
                    val tagRes = TagEndpoint.getInfo(tag)
                    tagInfo.value = tagRes

                },
                async {
                    val tagRes = TagEndpoint.getTopAlbums(tag)
                    if (tagRes.isNotEmpty()) {
                        val musRes = MusicorumAlbumEndpoint.fetchAlbums(tagRes)
                        if (musRes.isNotEmpty()) {
                            tagRes.onEachIndexed { index, tagAlbum ->
                                tagAlbum.images[0].url =
                                    musRes[index]?.bestResource?.bestImageUrl ?: ""
                            }
                        }
                    }
                    tagAlbums.value = tagRes
                },

                async {
                    val res = TagEndpoint.getTopArtists(tag)
                    val topArtist = res.firstOrNull()
                    topArtist?.let {
                        val musRes = MusicorumArtistEndpoint.fetchArtist(res)
                        if (musRes.isNotEmpty()) {
                            res.onEachIndexed { index, artist ->
                                artist.bestImageUrl = musRes[index].bestResource?.bestImageUrl ?: ""
                            }
                        }
                        topArtists.value = res
                        val bmp = getBitmap(it.bestImageUrl, ctx)
                        val palette = Palette.from(bmp).generate()
                        imagePalette.value = palette
                    }
                },
                async {
                    val res = TagEndpoint.getTopTracks(tag)
                    if (res.isNotEmpty()) {
                        val musRes = MusicorumTrackEndpoint.fetchTracks(res)
                        if (musRes.isNotEmpty()) {
                            res.onEachIndexed { index, track ->
                                track.bestImageUrl = musRes[index]?.bestResource?.bestImageUrl ?: ""
                            }
                        }
                        tracks.value = res
                    }
                }
            )

        }
    }

    init {
        init()
        Log.d("Tag VM", savedStateHandle.get<String>("tagName") ?: "missing tag")
    }
}