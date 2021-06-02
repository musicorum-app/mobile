package com.musicorumapp.mobile.api

import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.api.interceptors.LastfmKeyInterceptor
import com.musicorumapp.mobile.api.interceptors.LastfmMethodInterceptor
import com.musicorumapp.mobile.api.interceptors.SignedRequestInterceptor
import com.serjltt.moshi.adapters.FallbackOnNull
import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory



class MusicorumApi {
    companion object {
        fun getInstance(): Retrofit {

            val client = OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build()


            return Retrofit.Builder()
                .client(client)
                .baseUrl(Constants.MUSICORUM_RESOURCES_URL)
                .addConverterFactory(
                    MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(Wrapped.ADAPTER_FACTORY)
                        .add(FallbackOnNull.ADAPTER_FACTORY)
                        .build()
                ))
                .build()
        }

        fun getResourcesEndpoint(): MusicorumResourceEndpoint {
            return getInstance().create(MusicorumResourceEndpoint::class.java)
        }
    }
}