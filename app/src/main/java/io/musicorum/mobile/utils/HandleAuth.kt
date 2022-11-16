package io.musicorum.mobile.utils

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.musicorum.mobile.dataStore
import io.musicorum.mobile.ktor.endpoints.AuthEndpoint
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.serialization.User

suspend fun handleAuth(token: String, ctx: Context, userBlock: (user: User?, sessionKey: String) -> Unit) {
    val sessionKey = AuthEndpoint().getSession(token)?.session?.key
    sessionKey?.let {
        val dataStoreKey = stringPreferencesKey("session_key")
        Log.d("handleAuth", "calling getSessionUser")
        val user = UserEndpoint().getSessionUser(it)
        return userBlock(user, it)
    }
}

suspend fun commitDataStore(sessionKey: String, context: Context) {
    val dataStoreKey = stringPreferencesKey("session_key")
    context.dataStore.edit { prefs ->
        prefs[dataStoreKey] = sessionKey
    }
}
