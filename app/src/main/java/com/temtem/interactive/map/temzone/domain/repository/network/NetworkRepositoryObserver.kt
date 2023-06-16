package com.temtem.interactive.map.temzone.domain.repository.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.temtem.interactive.map.temzone.domain.repository.network.model.NetworkStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class NetworkRepositoryObserver @Inject constructor(
    private val application: Application,
) : NetworkRepository {

    private val connectivityManager: ConnectivityManager by lazy {
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun getStatus(): Flow<NetworkStatus> {
        return callbackFlow {
            val callbackFlow = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    launch {
                        send(NetworkStatus.AVAILABLE)
                    }
                }

                override fun onUnavailable() {
                    launch {
                        send(NetworkStatus.UNAVAILABLE)
                    }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    launch {
                        send(NetworkStatus.LOSING)
                    }
                }

                override fun onLost(network: Network) {
                    launch {
                        send(NetworkStatus.LOST)
                    }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callbackFlow)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callbackFlow)
            }
        }.distinctUntilChanged()
    }
}
