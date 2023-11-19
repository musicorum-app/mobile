package io.musicorum.mobile.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object UserData {
    const val DataStoreName = "userdata"
    val SESSION_KEY = stringPreferencesKey("session_key")
}