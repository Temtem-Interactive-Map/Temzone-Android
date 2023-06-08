package com.temtem.interactive.map.temzone.presentation.map.state

import com.temtem.interactive.map.temzone.domain.model.marker.Marker

sealed interface MapState {
    object Empty : MapState
    object Loading : MapState
    data class Success(
        val newMarkers: List<Marker>,
        val oldMarkers: List<Marker>,
    ) : MapState

    data class Error(
        val snackbarMessage: String,
        val networkAvailable: Boolean = true,
    ) : MapState
}
