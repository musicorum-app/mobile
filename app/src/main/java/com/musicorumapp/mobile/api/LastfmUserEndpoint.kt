package com.musicorumapp.mobile.api

import com.musicorumapp.mobile.api.interceptors.LastfmMethod
import com.musicorumapp.mobile.api.interceptors.SignedRequest
import com.musicorumapp.mobile.api.models.UserResponse
import com.serjltt.moshi.adapters.Wrapped
import retrofit2.http.GET
import retrofit2.http.Query

interface LastfmUserEndpoint {
    @GET("/")
    @LastfmMethod("user.getInfo")
    @SignedRequest
    @Wrapped(path = ["user"])
    suspend fun getUserInfoFromToken(
        @Query("sk") sessionToken: String
    ): UserResponse

    @GET("/")
    @LastfmMethod("user.getInfo")
    @Wrapped(path = ["user"])
    suspend fun getUserInfo(
        @Query("user") user: String
    ): UserResponse
}