package com.temtem.interactive.map.temzone.presentation.map.state

import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker

sealed interface MapState {
    object Empty : MapState
    object Loading : MapState
    data class Success(val markers: List<Marker>) : MapState
    data class Update(val newMarkers: List<Marker>, val oldMarkers: List<Marker>) : MapState
    data class Error(val snackbarMessage: String) : MapState
}
