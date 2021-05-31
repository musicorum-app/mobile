package com.musicorumapp.mobile.api.interceptors

import com.musicorumapp.mobile.Constants
import okhttp3.Interceptor
import okhttp3.Response

class LastfmKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newUrl = request.url()
            .newBuilder()
            .addPathSegment("2.0")
            .addQueryParameter("api_key", Constants.LASTFM_KEY)
            .addQueryParameter("format", "json")
            .build()

        return chain.proceed(request.newBuilder().url(newUrl).build())
    }
}