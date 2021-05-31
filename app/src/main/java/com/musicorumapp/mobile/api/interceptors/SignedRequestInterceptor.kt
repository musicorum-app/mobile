package com.musicorumapp.mobile.api.interceptors

import android.util.Log
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class SignedRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val invocation = request.tag(Invocation::class.java)
        val annotation = invocation?.method()?.getAnnotation(SignedRequest::class.java)

        if (annotation != null) {

            val params = mutableMapOf<String, String>()

            val url = request.url()

            url.queryParameterNames().forEach {
                params[it] = url.queryParameter(it).orEmpty()
            }

            Log.i(Constants.LOG_TAG, "sig: ${signRequest(params)}")

            val newUrl = url.newBuilder()
                .addQueryParameter("api_sig", signRequest(params))
                .build()

            return chain.proceed(request.newBuilder().url(newUrl).build())
        } else return chain.proceed(request)
    }

    private fun signRequest(params: MutableMap<String, String>): String {
        var sign = ""
        params.toSortedMap()
            .forEach { (k, v) ->
                if (!k.equals("format")) {
                    sign += k + v
                }
            }

        Log.i(Constants.LOG_TAG, "sig bef: ${sign + Constants.LASTFM_SECRET}")

        return Utils.md5Hash(sign + Constants.LASTFM_SECRET)
    }
}