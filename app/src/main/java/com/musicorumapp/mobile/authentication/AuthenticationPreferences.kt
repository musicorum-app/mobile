package com.musicorumapp.mobile.authentication

import android.content.SharedPreferences
import com.musicorumapp.mobile.PrefConstantKeys

class AuthenticationPreferences constructor(
    private val prefs: SharedPreferences
) {
    fun checkIfTokenExists(): Boolean {
        return prefs.contains(PrefConstantKeys.LASTFM_AUTH_KEY)
    }

    fun getLastfmSessionToken(): String? {
        return prefs.getString(PrefConstantKeys.LASTFM_AUTH_KEY, null)
    }

    fun setLastfmSessionToken(token: String?) {
        prefs.edit().putString(PrefConstantKeys.LASTFM_AUTH_KEY, token).apply()
    }
}