package com.temtem.interactive.map.temzone.domain.repository.network

import com.temtem.interactive.map.temzone.domain.repository.network.model.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun getStatus(): Flow<NetworkStatus>
}
