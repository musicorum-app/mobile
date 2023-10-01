package io.musicorum.mobile.router

import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.models.ResourceEntity
import io.musicorum.mobile.serialization.entities.Album
import io.musicorum.mobile.utils.PeriodResolver
import io.musicorum.mobile.views.individual.PartialAlbum
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Routes {
    fun user(username: String) = "user/$username"
    fun artist(name: String) = "artist/$name"
    const val home = "home"
    const val mostListened = "mostListened"
    fun album(data: String) = "album/$data"
    fun album(album: Album): String {
        val partial = PartialAlbum(album.name, album.artist ?: "unknown")
        return "album/${Json.encodeToString(partial)}"
    }
    const val settings = "settings"
    const val login = "login"
    const val scrobbleSettings = "settings/scrobble"
    fun albumTracklist(data: String) = "album/$data"
    const val scrobbling = "scrobbling"
    const val profile = "profile"
    fun track(data: String) = "track/$data"
    const val charts = "charts"
    fun tag(tagName: String) = "tag/$tagName"
    fun chartsDetail(index: Int) = "charts/detail?index=${index}"
    fun collage(entity: ResourceEntity? = null, period: FetchPeriod? = null): String {
        val periodString = period?.let { PeriodResolver.resolve(it) } ?: "7day"
        val entityString = entity?.entityName ?: "ARTIST"
        return "collage?period=$periodString&entity=$entityString"
    }
}