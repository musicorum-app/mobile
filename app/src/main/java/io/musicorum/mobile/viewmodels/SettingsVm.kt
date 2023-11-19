package io.musicorum.mobile.viewmodels

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import io.musicorum.mobile.models.PartialUser
import io.musicorum.mobile.repositories.LocalUserRepository
import io.musicorum.mobile.scrobblePrefs
import io.musicorum.mobile.userData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsVm(application: Application) : AndroidViewModel(application) {
    val enabledApps = MutableLiveData<Set<String>>()
    val deviceScrobble = MutableLiveData(false)
    val user = MutableLiveData<PartialUser?>(null)
    val showReport = MutableLiveData(false)
    val ctx = application

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            ctx.applicationContext.userData.edit {
                it.remove(stringPreferencesKey("session_key"))
            }
            onLogout()
        }
    }

    fun launchUrl(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    }

    fun getScrobbleInfo() {
        viewModelScope.launch {
            val enabled = ctx.scrobblePrefs.data.map { p ->
                p[booleanPreferencesKey("enabled")]
            }.first()
            deviceScrobble.value = enabled

            val apps = ctx.scrobblePrefs.data.map { p ->
                p[stringSetPreferencesKey("enabledApps")]
            }.first()
            enabledApps.value = apps
        }
    }

    init {
        showReport.value = Firebase.crashlytics.didCrashOnPreviousExecution()
        viewModelScope.launch {
            val localUser = LocalUserRepository(ctx).getUser()
            user.value = localUser
        }
    }
}