package io.musicorum.mobile.router

object Routes {
    fun user(username: String) = "user/$username"
    fun artist(name: String) = "artist/$name"
    const val home = "home"
    const val mostListened = "mostListened"
    fun album(data: String) = "album/$data"
    const val settings = "settings"
    const val settingsScrobble = "settings/scrobble"
    fun albumTracklist(data: String) = "album/$data"
    const val scrobbling = "scrobbling"
    const val profile = "profile"
    fun track(data: String) = "track/$data"
}