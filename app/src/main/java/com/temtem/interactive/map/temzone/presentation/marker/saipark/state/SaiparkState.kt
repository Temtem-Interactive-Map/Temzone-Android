package com.temtem.interactive.map.temzone.presentation.marker.saipark.state

import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.Saipark

sealed interface SaiparkState {
    object Loading : SaiparkState
    data class Success(
        val saipark: Saipark,
    ) : SaiparkState

    data class Error(val snackbarMessage: String) : SaiparkState
}
