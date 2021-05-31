package com.musicorumapp.mobile.api.models

import com.google.gson.annotations.SerializedName
import com.musicorumapp.mobile.utils.Utils

class Artist (
    val name: String,
    val listeners: Int,
): SearchableItem

data class LastfmArtistSearchResponseRoot(
    val results: LastfmArtistSearchResponse
) {
    data class LastfmArtistSearchResponse(
        @SerializedName("opensearch:totalResults")
        val totalResults: Any,

        @SerializedName("opensearch:startIndex")
        val startIndex: Any,

        @SerializedName("artistmatches")
        val matches: LastfmArtistSearchMatchesResponse
    ) {
        data class LastfmArtistSearchMatchesResponse(
            val artist: List<LastfmArtistSearchMatchesItemResponse>
        ) {
            data class LastfmArtistSearchMatchesItemResponse(
                val name: String,
                val listeners: Any,
            ) {
                fun toArtist(): Artist {
                    return Artist(
                        name = name,
                        listeners =  Utils.anyToInt(listeners)
                    )
                }
            }
        }
    }
}