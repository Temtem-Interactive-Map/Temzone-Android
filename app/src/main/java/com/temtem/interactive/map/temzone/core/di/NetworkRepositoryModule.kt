package com.temtem.interactive.map.temzone.core.di

import com.temtem.interactive.map.temzone.domain.repository.network.NetworkRepository
import com.temtem.interactive.map.temzone.domain.repository.network.NetworkRepositoryObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(networkRepositoryObserver: NetworkRepositoryObserver): NetworkRepository
}
