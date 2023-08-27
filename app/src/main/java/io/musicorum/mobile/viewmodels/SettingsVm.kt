package io.musicorum.mobile.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.musicorum.mobile.scrobblePrefs
import io.musicorum.mobile.userData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsVm(application: Application) : AndroidViewModel(application) {
    val enabledApps = MutableLiveData<Set<String>>()
    val deviceScrobble = MutableLiveData(false)

    @SuppressLint("StaticFieldLeak")
    private val context = application as Context

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            context.applicationContext.userData.edit {
                it.remove(stringPreferencesKey("session_key"))
            }
            onLogout()
        }
    }

    fun launchUrl(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun getScrobbleInfo() {
        viewModelScope.launch {
            val enabled = context.scrobblePrefs.data.map { p ->
                p[booleanPreferencesKey("enabled")]
            }.first()
            deviceScrobble.value = enabled

            val apps = context.scrobblePrefs.data.map { p ->
                p[stringSetPreferencesKey("enabledApps")]
            }.first()
            enabledApps.value = apps
        }
    }
}