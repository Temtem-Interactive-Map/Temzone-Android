package com.temtem.interactive.map.temzone.domain.interceptor

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheInterceptor @Inject constructor(
    private val application: Application,
) : Interceptor {

    private companion object {
        private const val MAX_AGE = 4 * 60 * 60
        private const val MAX_STALE = 7 * 24 * 60 * 60
    }

    private val connectivityManager: ConnectivityManager by lazy {
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (hasNetwork()) {
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .header("Cache-Control", "public, max-age=$MAX_AGE")
                    .build()
            )
        } else {
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$MAX_STALE")
                    .build()
            )
        }
    }

    private fun hasNetwork(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}
