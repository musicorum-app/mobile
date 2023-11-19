package io.musicorum.mobile.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object ScrobblePreferences {
    const val DataStoreName = "scrobblePrefs"
    val SCROBBLE_POINT_KEY = floatPreferencesKey("scrobblePoint")
    val ENABLED_KEY = booleanPreferencesKey("enabled")
    val ALLOWED_APPS_KEY = stringSetPreferencesKey("enabledApps")
    val UPDATED_NOWPLAYING_KEY = booleanPreferencesKey("updateNowPlaying")
}