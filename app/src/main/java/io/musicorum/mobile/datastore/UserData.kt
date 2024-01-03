package io.musicorum.mobile.datastore

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object UserData {
    const val DataStoreName = "userdata"
    val SESSION_KEY = stringPreferencesKey("session_key")
    val PINNED_USERS = stringSetPreferencesKey("pinned_users")
}