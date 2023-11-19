package io.musicorum.mobile.datastore

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object LocalUser {
    const val DataStoreName = "partialUser"
    val USERNAME_KEY = stringPreferencesKey("usernameArg")
    val PROFILE_ICON_KEY = stringPreferencesKey("profilePictureUrl")
    val EXPIRES_IN_KEY = longPreferencesKey("expires_in")
}