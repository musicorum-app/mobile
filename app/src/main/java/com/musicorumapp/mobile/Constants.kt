package com.musicorumapp.mobile

object Constants {
    const val LOG_TAG = "MusicorumApp"

    const val LASTFM_KEY: String = BuildConfig.LASTFM_KEY
    const val LASTFM_SECRET: String = BuildConfig.LASTFM_SECRET

    const val AUTH_PREFS_KEY = "musicorum_app_auth_shared"

    const val MUSICORUM_RESOURCES_URL = "https://resource.musicorumapp.com"
    const val MUSICORUM_URL_SCHEME = "musicorum"
    val MUSICORUM_LOGIN_URL = "https://www.last.fm/api/auth?api_key=$LASTFM_KEY&cb=$MUSICORUM_URL_SCHEME://callback"

    const val DEFAULT_ARTIST_IMAGE_URL = "https://lastfm.freetls.fastly.net/i/u/300x300/2a96cbd8b46e442fc41c2b86b821562f.png"
}

object PrefConstantKeys {
    const val LASTFM_AUTH_KEY = "lastfm_auth_key"
}