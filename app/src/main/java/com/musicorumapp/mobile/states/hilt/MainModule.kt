package com.musicorumapp.mobile.states.hilt

import android.content.Context
import com.musicorumapp.mobile.Constants
import com.musicorumapp.mobile.api.LastfmApi
import com.musicorumapp.mobile.api.LastfmArtistEndpoint
import com.musicorumapp.mobile.authentication.AuthenticationPreferences
import com.musicorumapp.mobile.repos.ArtistRepository
import com.musicorumapp.mobile.states.SessionState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Singleton
    @Provides
    fun provideLastfmArtistEndpoint (): LastfmArtistEndpoint = LastfmApi.getArtistEndpoint()

    @Singleton
    @Provides
    fun provideAuthenticationPreferences(@ApplicationContext context: Context)
    : AuthenticationPreferences = AuthenticationPreferences(context.getSharedPreferences(Constants.AUTH_PREFS_KEY, Context.MODE_PRIVATE))

    @Singleton
    @Provides
    fun provideSessionState (@ApplicationContext context: Context): SessionState = SessionState(context)
}