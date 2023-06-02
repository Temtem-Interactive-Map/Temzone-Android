package com.temtem.interactive.map.temzone.repositories.temzone.retrofit

import com.temtem.interactive.map.temzone.repositories.auth.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            return@runBlocking authRepository.getUserToken()
        }

        return chain.proceed(
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        )
    }
}
