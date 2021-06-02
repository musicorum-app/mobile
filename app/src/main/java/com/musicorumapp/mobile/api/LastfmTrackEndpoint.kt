package com.musicorumapp.mobile.api

import com.musicorumapp.mobile.api.interceptors.LastfmMethod
import com.musicorumapp.mobile.api.models.LastfmTrackSearchResponse
import com.serjltt.moshi.adapters.Wrapped
import retrofit2.http.GET
import retrofit2.http.Query

interface LastfmTrackEndpoint {
    @GET("/")
    @LastfmMethod("track.search")
    @Wrapped(path = ["results"])
    suspend fun searchTracks(
        @Query("track") query: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): LastfmTrackSearchResponse
}