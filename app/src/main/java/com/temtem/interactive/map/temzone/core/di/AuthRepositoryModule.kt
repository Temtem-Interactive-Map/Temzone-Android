package com.temtem.interactive.map.temzone.core.di

import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepository
import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepositoryFirebase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryFirebase: AuthRepositoryFirebase): AuthRepository
}
