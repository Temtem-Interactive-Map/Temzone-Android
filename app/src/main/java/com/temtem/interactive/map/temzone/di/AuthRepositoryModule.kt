package com.temtem.interactive.map.temzone.di

import com.temtem.interactive.map.temzone.repositories.auth.AuthRepository
import com.temtem.interactive.map.temzone.repositories.auth.FirebaseAuthRepository
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
    abstract fun bindAuthRepository(firebaseAuthRepository: FirebaseAuthRepository): AuthRepository
}
