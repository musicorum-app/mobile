package io.musicorum.mobile.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.repositories.RecentTracksRepository
import io.musicorum.mobile.serialization.entities.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecentSrcobblesViewModel(application: Application) : AndroidViewModel(application) {
    val ctx = application as Context
    val user = MutableLiveData<PartialUser>(null)
    val recentTracks = MutableLiveData<Flow<PagingData<Track>>>(null)

    private fun fetchRecentTracks(username: String): Flow<PagingData<Track>> =
        RecentTracksRepository.getRecentTracks(username).cachedIn(viewModelScope)

    private fun init() {
        viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            user.value = localUser
            val flow = fetchRecentTracks(localUser.username)
            recentTracks.value = flow
        }
    }

    init {
        init()
        val analytics = FirebaseAnalytics.getInstance(ctx)
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "recent_scrobbles")
        }
    }
}