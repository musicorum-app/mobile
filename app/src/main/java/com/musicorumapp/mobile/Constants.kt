package com.musicorumapp.mobile

object Constants {
    const val LOG_TAG = "MusicorumApp"

    const val LASTFM_KEY = BuildConfig.LASTFM_KEY
    const val LASTFM_SECRET = BuildConfig.LASTFM_SECRET

    const val AUTH_PREFS_KEY = "musicorum_app_auth_shared"

    const val MUSICORUM_API_URL = "https://api.musicorumapp.com/mobile"
    const val MUSICORUM_URL_SCHEME = "musicorum"
    const val MUSICORUM_LOGIN_URL = "https://www.last.fm/api/auth?api_key=$LASTFM_KEY&cb=$MUSICORUM_URL_SCHEME://callback"
}

object PrefConstantKeys {
    const val LASTFM_AUTH_KEY = "lastfm_auth_key"
}