package com.temtem.interactive.map.temzone.core.di

import com.temtem.interactive.map.temzone.domain.repository.temzone.TemzoneRepository
import com.temtem.interactive.map.temzone.domain.repository.temzone.TemzoneRepositoryRetrofit
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TemzoneRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTemzoneRepository(temzoneRepositoryRetrofit: TemzoneRepositoryRetrofit): TemzoneRepository
}
