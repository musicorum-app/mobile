package com.musicorumapp.mobile.api

import com.musicorumapp.mobile.api.interceptors.LastfmMethod
import com.musicorumapp.mobile.api.interceptors.SignedRequest
import com.musicorumapp.mobile.api.models.AuthSessionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LastfmAuthEndpoint {
    @GET("/")
    @LastfmMethod("auth.getSession")
    @SignedRequest
    suspend fun getSession(
        @Query("token") token: String
    ): AuthSessionResponse
}