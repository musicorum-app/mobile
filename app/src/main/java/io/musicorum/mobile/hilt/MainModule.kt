package io.musicorum.mobile.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.musicorum.mobile.repositories.ScrobbleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {
    @Singleton
    @Provides
    fun provideRecentScrobbles(): ScrobbleRepository = ScrobbleRepository()
}