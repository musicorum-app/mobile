package io.musicorum.mobile.screens.mostListened

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.musicorum.mobile.ktor.endpoints.UserEndpoint
import io.musicorum.mobile.serialization.Track
import java.time.Instant

class RecentTracksPageSource(val user: String) : PagingSource<Int, Track>() {
    override fun getRefreshKey(state: PagingState<Int, Track>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> {
        return try {
            val page = params.key ?: 1
            val from = "${Instant.now().minusSeconds(604800).toEpochMilli() / 1000}"
            val res = UserEndpoint.getRecentTracks(user, page = page, from = from, extended = true)
            LoadResult.Page(
                data = res!!.recentTracks.tracks,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (res.recentTracks.tracks.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}