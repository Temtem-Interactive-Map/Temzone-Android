package com.temtem.interactive.map.temzone.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.repositories.auth.AuthRepository
import com.temtem.interactive.map.temzone.repositories.temzone.TemzoneRepository
import com.temtem.interactive.map.temzone.repositories.temzone.data.MarkerType
import com.temtem.interactive.map.temzone.ui.map.state.MarkersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val temzoneRepository: TemzoneRepository,
) : ViewModel() {

    private val _markersUiState: MutableStateFlow<MarkersUiState> =
        MutableStateFlow(MarkersUiState.Empty)
    val markersUiState: SharedFlow<MarkersUiState> = _markersUiState.asSharedFlow()

    fun initializeMap() {
        _markersUiState.value = MarkersUiState.Loading

        viewModelScope.launch {
            try {
                val markers = temzoneRepository.getMarkers()

                _markersUiState.value = MarkersUiState.Success(markers, emptyList())
            } catch (e: Exception) {
                _markersUiState.value = MarkersUiState.Error(e.message.orEmpty())
            }
        }
    }

    fun searchMarkers(query: String) {

    }

    private val _temtemLayerUiState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val temtemLayerUiState: StateFlow<Boolean> = _temtemLayerUiState.asStateFlow()

    fun changeTemtemLayerVisibility() {
        _temtemLayerUiState.value = !_temtemLayerUiState.value

        changeLayerVisibility(_temtemLayerUiState.value, listOf(MarkerType.SPAWN))
    }

    private val _landmarkLayerUiState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val landmarkLayerUiState: StateFlow<Boolean> = _landmarkLayerUiState.asStateFlow()

    fun changeLandmarkLayerVisibility() {
        _landmarkLayerUiState.value = !_landmarkLayerUiState.value

        changeLayerVisibility(_landmarkLayerUiState.value, listOf(MarkerType.SAIPARK))
    }

    private fun changeLayerVisibility(visible: Boolean, types: List<MarkerType>) {
        val markersUiState = _markersUiState.value as MarkersUiState.Success

        if (visible) {
            val oldMarkers = markersUiState.oldMarkers.toMutableList()
            val newMarkers = oldMarkers.filter { it.type in types }

            oldMarkers.removeAll(newMarkers)

            _markersUiState.value = MarkersUiState.Success(
                markersUiState.newMarkers + newMarkers,
                oldMarkers,
            )
        } else {
            val newMarkers = markersUiState.newMarkers.toMutableList()
            val oldMarkers = newMarkers.filter { it.type in types }

            newMarkers.removeAll(oldMarkers)

            _markersUiState.value = MarkersUiState.Success(
                newMarkers,
                markersUiState.oldMarkers + oldMarkers,
            )
        }
    }

    fun isUserSignedIn(): Boolean {
        return authRepository.isUserSignedIn()
    }

    fun signOut() {
        _markersUiState.value = MarkersUiState.Empty
        authRepository.signOut()
    }
}
