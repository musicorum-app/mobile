package com.musicorumapp.mobile.api

import com.musicorumapp.mobile.api.interceptors.LastfmMethod
import com.musicorumapp.mobile.api.interceptors.SignedRequest
import com.musicorumapp.mobile.api.models.AuthSessionResponse
import com.musicorumapp.mobile.api.models.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LastfmUserEndpoint {
    @GET("/")
    @LastfmMethod("user.getInfo")
    @SignedRequest
    suspend fun getUserInfo(
        @Query("sk") sessionToken: String
    ): UserResponse
}