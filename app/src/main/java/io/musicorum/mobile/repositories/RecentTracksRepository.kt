package io.musicorum.mobile.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import io.musicorum.mobile.screens.mostListened.RecentTracksPageSource

object RecentTracksRepository {
    fun getRecentTracks(user: String) = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { RecentTracksPageSource(user) }
    ).flow
}