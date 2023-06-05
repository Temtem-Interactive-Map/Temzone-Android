package com.temtem.interactive.map.temzone.presentation.map.state

import com.temtem.interactive.map.temzone.domain.model.marker.Marker

sealed interface MarkersState {
    object Empty : MarkersState
    object Loading : MarkersState
    data class Success(
        val newMarkers: List<Marker>,
        val oldMarkers: List<Marker>,
    ) : MarkersState

    data class Error(
        val snackbarMessage: String,
        val networkAvailable: Boolean = true,
    ) : MarkersState
}
