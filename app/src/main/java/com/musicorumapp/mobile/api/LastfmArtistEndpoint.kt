package com.musicorumapp.mobile.api

import com.musicorumapp.mobile.api.interceptors.LastfmMethod
import com.musicorumapp.mobile.api.interceptors.SignedRequest
import com.musicorumapp.mobile.api.models.*
import com.serjltt.moshi.adapters.Wrapped
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LastfmArtistEndpoint {
    @GET("/")
    @LastfmMethod("artist.search")
    @Wrapped(path = ["results"])
    suspend fun searchArtists(
        @Query("artist") query: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): LastfmArtistSearchResponse


    @GET("/")
    @LastfmMethod("artist.getInfo")
    @Wrapped(path = ["artist"])
    suspend fun artistGetInfo(
        @Query("artist") artist: String,
        @Query("user") user: String,
        @Query("lang") lang: String? = null
    ): LastfmArtistInfoResponse


    @GET("/")
    @LastfmMethod("artist.getTopTags")
    @Wrapped(path = ["toptags", "tag"])
    suspend fun getAlbumTopTags(
        @Query("album") query: String,
        @Query("artist") artist: String
    ): List<TagResponseItem>


    @POST("/")
    @LastfmMethod("artist.addTags")
    @SignedRequest
    suspend fun addTags(
        @Query("artist") artist: String,
        @Query("tags") tags: String,
        @Query("sk") sk: String,
    )


    @POST("/")
    @LastfmMethod("artist.removeTags")
    @SignedRequest
    suspend fun removeTag(
        @Query("artist") artist: String,
        @Query("tag") tag: String,
        @Query("sk") sk: String,
    )


    @GET("/")
    @LastfmMethod("artist.getTopAlbums")
    @Wrapped(path = ["topalbums", "album"])
    suspend fun getTopAlbums(
        @Query("artist") artist: String
    ): List<LastfmAlbumFromArtistTopAlbumsResponseItem>


    @GET("/")
    @LastfmMethod("artist.getTopTracks")
    @Wrapped(path = ["toptracks", "track"])
    suspend fun getTopTracks(
        @Query("artist") artist: String
    ): List<TrackFromArtistTopTracksItem>
}