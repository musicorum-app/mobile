package io.musicorum.mobile.router

import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.models.ResourceEntity
import io.musicorum.mobile.utils.PeriodResolver

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
    const val charts = "charts"
    fun chartsDetail(index: Int) = "charts/detail?index=${index}"
    fun collage(entity: ResourceEntity? = null, period: FetchPeriod? = null): String {
        val periodString = period?.let { PeriodResolver.resolve(it) } ?: "7day"
        val entityString = entity?.entityName ?: "ARTIST"
        return "collage?period=$periodString&entity=$entityString"
    }
}