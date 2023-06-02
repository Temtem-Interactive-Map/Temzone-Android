package com.temtem.interactive.map.temzone.di

import com.temtem.interactive.map.temzone.repositories.temzone.RetrofitTemzoneRepository
import com.temtem.interactive.map.temzone.repositories.temzone.TemzoneRepository
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
    abstract fun bindTemzoneRepository(retrofitTemzoneRepository: RetrofitTemzoneRepository): TemzoneRepository
}
