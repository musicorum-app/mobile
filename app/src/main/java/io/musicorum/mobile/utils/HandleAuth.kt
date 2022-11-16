package io.musicorum.mobile.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.musicorum.mobile.dataStore
import io.musicorum.mobile.ktor.endpoints.AuthEndpoint
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.serialization.User

suspend fun handleAuth(token: String, userBlock: (user: User?, sessionKey: String) -> Unit) {
    val sessionKey = AuthEndpoint().getSession(token)?.session?.key
    sessionKey?.let {
        val user = UserEndpoint().getSessionUser(it)
        return userBlock(user, it)
    }
}

suspend fun commitUser(sessionKey: String, context: Context) {
    val dataStoreKey = stringPreferencesKey("session_key")
    context.dataStore.edit { prefs ->
        prefs[dataStoreKey] = sessionKey
    }
}
