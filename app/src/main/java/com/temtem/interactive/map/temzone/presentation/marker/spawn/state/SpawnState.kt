package com.temtem.interactive.map.temzone.presentation.marker.spawn.state

import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.Spawn

sealed interface SpawnState {
    object Loading : SpawnState
    data class Success(val spawn: Spawn) : SpawnState
    data class Error(val message: String) : SpawnState
}
