package io.musicorum.mobile.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import io.musicorum.mobile.datastore.ScrobblePreferences
import io.musicorum.mobile.scrobblePrefs
import io.musicorum.mobile.views.settings.MediaApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class ScrobbleSettingsVm(application: Application) : AndroidViewModel(application) {
    private val ctx = application as Context
    private val packageManager = ctx.packageManager

    val scrobblePoint = MutableLiveData(50f)
    val updateNowPlaying = MutableLiveData(false)
    val enabledPackageSet: MutableLiveData<Set<String>> = MutableLiveData(emptySet())
    val isEnabled = MutableLiveData(false)
    val availableApps: MutableLiveData<List<MediaApp>> = MutableLiveData()
    val hasPermission = MutableLiveData(false)
    val showSpotifyModal = MutableLiveData(false)


    fun updateScrobbling(value: Boolean) {
        isEnabled.value = value
        if (value) {
            Firebase.analytics.logEvent("enable_device_scrobbling", null)
        } else {
            Firebase.analytics.logEvent("disable_device_scrobbling", null)
        }
        viewModelScope.launch {
            ctx.scrobblePrefs.edit { p ->
                p[ScrobblePreferences.ENABLED_KEY] = value
            }
        }
    }

    fun updateNowPlaying(value: Boolean) {
        updateNowPlaying.value = value
        viewModelScope.launch {
            ctx.scrobblePrefs.edit { p ->
                p[ScrobblePreferences.UPDATED_NOWPLAYING_KEY] = value
            }
        }
    }

    fun updateScrobblePoint() {
        viewModelScope.launch {
            ctx.applicationContext.scrobblePrefs.edit { p ->
                p[ScrobblePreferences.SCROBBLE_POINT_KEY] = scrobblePoint.value!!
            }
        }
    }

    fun enableSpotify() {
        val newSet = enabledPackageSet.value?.toMutableSet() ?: mutableSetOf()
        newSet.add("com.spotify.music")
        viewModelScope.launch {
            ctx.scrobblePrefs.edit { p ->
                p[ScrobblePreferences.ALLOWED_APPS_KEY] = newSet
            }
        }
        enabledPackageSet.value = newSet
    }

    fun updateMediaApp(pkg: String, state: Boolean) {
        if (pkg == "com.spotify.music" && state) {
            showSpotifyModal.value = true
            return
        }
        val newSet = enabledPackageSet.value?.toMutableSet() ?: mutableSetOf()
        if (state) {
            newSet.add(pkg)
        } else {
            newSet.remove(pkg)
        }

        viewModelScope.launch {
            ctx.scrobblePrefs.edit { p ->
                p[ScrobblePreferences.ALLOWED_APPS_KEY] = newSet
            }
            enabledPackageSet.value = newSet
        }
    }

    private fun init() {
        viewModelScope.launch {
            val scrobblePointData =
                ctx.scrobblePrefs.data.map { p -> p[ScrobblePreferences.SCROBBLE_POINT_KEY] }
                    .first()
            val enabled = ctx.scrobblePrefs.data.map { p -> p[ScrobblePreferences.ENABLED_KEY] }
                .first()
            val enabledApps =
                ctx.scrobblePrefs.data.map { p -> p[ScrobblePreferences.ALLOWED_APPS_KEY] }
                    .first()
            val updateNowPlayingData =
                ctx.scrobblePrefs.data.map { p -> p[ScrobblePreferences.UPDATED_NOWPLAYING_KEY] }
                    .first()

            if (scrobblePointData == null) {
                scrobblePoint.value = 50f
                updateScrobblePoint()
            } else {
                scrobblePoint.value = scrobblePointData
            }

            isEnabled.value = enabled
            updateNowPlaying.value = updateNowPlayingData
            enabledPackageSet.value = enabledApps

            val list = mutableListOf<MediaApp>()
            packageManager.getInstalledApplications(0).forEach { appInfo ->
                if (appInfo.category >= ApplicationInfo.CATEGORY_AUDIO) {
                    val appLogo = packageManager.getApplicationIcon(appInfo)
                    val appName = packageManager.getApplicationLabel(appInfo)
                    list.add(MediaApp(appName.toString(), appInfo.packageName, appLogo))
                }
            }
            availableApps.value = list
        }
    }

    fun checkNotificationPermission() {
        hasPermission.value = NotificationManagerCompat
            .getEnabledListenerPackages(ctx)
            .contains(ctx.packageName)
    }

    init {
        init()
    }
}