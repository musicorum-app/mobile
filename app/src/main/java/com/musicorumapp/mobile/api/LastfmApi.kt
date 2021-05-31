package com.musicorumapp.mobile.api

import com.musicorumapp.mobile.api.interceptors.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.musicorumapp.mobile.api.models.Artist
import com.musicorumapp.mobile.api.models.SearchController
import okhttp3.logging.HttpLoggingInterceptor


private const val LASTFM_API_URL = "https://ws.audioscrobbler.com/"

class LastfmApi {
    companion object {
        fun getInstance(): Retrofit {

            val client = OkHttpClient.Builder()
                .addInterceptor(LastfmKeyInterceptor())
                .addInterceptor(LastfmMethodInterceptor())
                .addInterceptor(SignedRequestInterceptor())
                .addInterceptor(
                    HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build()


            return Retrofit.Builder()
                .client(client)
                .baseUrl(LASTFM_API_URL)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder()
                            .setLenient()
                            .create()
                    )
                )
                .build()
        }

        fun getAuthEndpoint(): LastfmAuthEndpoint {
            return getInstance().create(LastfmAuthEndpoint::class.java)
        }

        fun getUserEndpoint(): LastfmUserEndpoint {
            return getInstance().create(LastfmUserEndpoint::class.java)
        }

        fun getArtistEndpoint(): LastfmArtistEndpoint {
            return getInstance().create(LastfmArtistEndpoint::class.java)
        }

        suspend fun searchArtists(query: String, perPage: Int = 50): SearchController<Artist> {
            val controller = SearchController(
                perPage = perPage,
                searchMethod = { pg ->
                    val items = getArtistEndpoint().searchArtists(query, perPage, pg)

                    items.results.matches.artist.map { it.toArtist() }
                }
            )

            controller.doSearch(1)

            return controller
        }
    }
}