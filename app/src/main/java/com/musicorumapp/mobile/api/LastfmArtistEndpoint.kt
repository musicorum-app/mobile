package com.musicorumapp.mobile.api

import com.musicorumapp.mobile.api.interceptors.LastfmMethod
import com.musicorumapp.mobile.api.interceptors.SignedRequest
import com.musicorumapp.mobile.api.models.LastfmArtistSearchResponseRoot
import com.musicorumapp.mobile.api.models.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LastfmArtistEndpoint {
    @GET("/")
    @LastfmMethod("artist.search")
    suspend fun searchArtists(
        @Query("artist") query: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): LastfmArtistSearchResponseRoot
}