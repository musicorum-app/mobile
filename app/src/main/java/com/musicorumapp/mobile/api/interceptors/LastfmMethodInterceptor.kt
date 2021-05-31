package com.musicorumapp.mobile.api.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class LastfmMethodInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val invocation = request.tag(Invocation::class.java)
        val annotation = invocation?.method()?.getAnnotation(LastfmMethod::class.java)

        if (annotation != null) {
            val method = annotation.method

            val newUrl = request.url()
                .newBuilder()
                .addQueryParameter("method", method)
                .build()

            return chain.proceed(request.newBuilder().url(newUrl).build())
        } else return chain.proceed(request)
    }
}