package com.musicorumapp.mobile.api

import com.musicorumapp.mobile.api.interceptors.LastfmMethod
import com.musicorumapp.mobile.api.interceptors.SignedRequest
import com.musicorumapp.mobile.api.models.*
import com.serjltt.moshi.adapters.Wrapped
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LastfmAlbumEndpoint {
    @GET("/")
    @LastfmMethod("album.search")
    @Wrapped(path = ["results"])
    suspend fun searchAlbums(
        @Query("album") query: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): LastfmAlbumSearchResponse

    @GET("/")
    @LastfmMethod("album.getInfo")
    @Wrapped(path = ["album"])
    suspend fun getAlbumInfo(
        @Query("album") query: String,
        @Query("artist") artist: String,
        @Query("user") user: String?
    ): LastfmAlbumInfoResponse

    @GET("/")
    @LastfmMethod("album.getTopTags")
    @Wrapped(path = ["toptags", "tag"])
    suspend fun getAlbumTopTags(
        @Query("album") query: String,
        @Query("artist") artist: String
    ): List<TagResponseItem>

    @POST("/")
    @LastfmMethod("album.addTags")
    @SignedRequest
    suspend fun addTags(
        @Query("artist") artist: String,
        @Query("album") album: String,
        @Query("tags") tags: String,
        @Query("sk") sk: String,
    )

    @POST("/")
    @LastfmMethod("album.removeTags")
    @SignedRequest
    suspend fun removeTag(
        @Query("artist") artist: String,
        @Query("album") album: String,
        @Query("tag") tag: String,
        @Query("sk") sk: String,
    )
}