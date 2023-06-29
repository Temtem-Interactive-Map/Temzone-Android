package com.temtem.interactive.map.temzone.domain.interceptor

import com.temtem.interactive.map.temzone.domain.repository.network.NetworkRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheInterceptor @Inject constructor(
    private val networkRepository: NetworkRepository,
) : Interceptor {

    private companion object {
        private const val MAX_AGE = 4 * 60 * 60
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            if (networkRepository.hasNetworkConnection()) {
                chain.request()
                    .newBuilder()
                    .header("Cache-Control", "public, max-age=$MAX_AGE")
                    .build()
            } else chain.request()
        )
    }
}
