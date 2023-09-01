package io.musicorum.mobile.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.repositories.LocalUserRepository
import kotlinx.coroutines.launch

class AccountVm(application: Application) : AndroidViewModel(application) {
    val user = MutableLiveData<PartialUser>(null)
    val ctx = application as Context

    private fun init() {
        viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            user.value = localUser
        }
    }

    init {
        val analytics = FirebaseAnalytics.getInstance(ctx)
        init()
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "user_own_profile")
        }
    }
}