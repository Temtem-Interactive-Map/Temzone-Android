package com.temtem.interactive.map.temzone.core.di

import com.temtem.interactive.map.temzone.domain.repository.preference.PreferenceRepository
import com.temtem.interactive.map.temzone.domain.repository.preference.PreferenceRepositoryDatastore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPreferenceRepository(preferenceRepositoryDatastore: PreferenceRepositoryDatastore): PreferenceRepository
}
