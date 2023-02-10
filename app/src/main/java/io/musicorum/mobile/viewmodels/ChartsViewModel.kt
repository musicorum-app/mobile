package io.musicorum.mobile.viewmodels

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.ktor.endpoints.FetchPeriod
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.serialization.TopArtist
import io.musicorum.mobile.utils.createPalette
import io.musicorum.mobile.utils.getBitmap
import kotlinx.coroutines.launch

class ChartsViewModel : ViewModel() {
    val preferredColor: MutableLiveData<Color> = MutableLiveData()
    val topArtists: MutableLiveData<List<TopArtist>> = MutableLiveData()

    fun getColor(image: String, ctx: Context) {
        viewModelScope.launch {
            val bmp = getBitmap(image, ctx)
            val palette = createPalette(bmp)
            preferredColor.value = Color(palette.getVibrantColor(Color.Gray.toArgb()))
        }
    }

    fun getTopArtists(user: String, period: FetchPeriod) {
        viewModelScope.launch {
            val res = UserEndpoint.getTopArtists(username = user, period = period, limit = 5)
            res?.let {
                topArtists.value = it.topArtists.artists
            }
        }
    }
}