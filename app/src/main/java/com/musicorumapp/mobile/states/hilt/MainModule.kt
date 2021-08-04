package com.musicorumapp.mobile.states.hilt

import com.musicorumapp.mobile.api.LastfmApi
import com.musicorumapp.mobile.api.LastfmArtistEndpoint
import com.musicorumapp.mobile.repos.ArtistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Singleton
    @Provides
    fun provideLastfmArtistEndpoint (): LastfmArtistEndpoint {
        return LastfmApi.getArtistEndpoint()
    }

    @Singleton
    @Provides
    fun provideArtistRepository (): ArtistRepository = ArtistRepository()
}