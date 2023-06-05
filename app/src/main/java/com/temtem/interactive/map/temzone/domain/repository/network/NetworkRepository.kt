package com.temtem.interactive.map.temzone.domain.repository.network

import com.temtem.interactive.map.temzone.domain.model.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun observe(): Flow<NetworkStatus>
}
