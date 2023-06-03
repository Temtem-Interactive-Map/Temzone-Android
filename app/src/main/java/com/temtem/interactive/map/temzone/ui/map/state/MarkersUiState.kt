package com.temtem.interactive.map.temzone.ui.map.state

import com.temtem.interactive.map.temzone.repositories.temzone.data.Marker

sealed interface MarkersUiState {

    object Empty : MarkersUiState

    object Loading : MarkersUiState

    data class Success(
        val newMarkers: List<Marker>,
        val oldMarkers: List<Marker>,
    ) : MarkersUiState

    data class Error(val message: String) : MarkersUiState
}
