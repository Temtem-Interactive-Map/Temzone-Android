package com.temtem.interactive.map.temzone.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.domain.exception.NetworkException
import com.temtem.interactive.map.temzone.domain.model.NetworkStatus
import com.temtem.interactive.map.temzone.domain.model.marker.Marker
import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepository
import com.temtem.interactive.map.temzone.domain.repository.network.NetworkRepository
import com.temtem.interactive.map.temzone.domain.repository.temzone.TemzoneRepository
import com.temtem.interactive.map.temzone.presentation.map.state.MarkersState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val temzoneRepository: TemzoneRepository,
    private val networkRepository: NetworkRepository,
) : ViewModel() {

    private val _markersState: MutableStateFlow<MarkersState> = MutableStateFlow(MarkersState.Empty)
    val markersState: SharedFlow<MarkersState> = _markersState.asSharedFlow()

    init {
        viewModelScope.launch {
            networkRepository.observe().collect {
                if (it == NetworkStatus.AVAILABLE && _markersState.value is MarkersState.Error && !(_markersState.value as MarkersState.Error).networkAvailable) {
                    getMarkers()
                }
            }
        }
    }

    fun getMarkers() {
        _markersState.value = MarkersState.Loading

        viewModelScope.launch {
            try {
                val markers = temzoneRepository.getMarkers()

                _markersState.value = MarkersState.Success(markers, emptyList())
            } catch (exception: Exception) {
                when (exception) {
                    is NetworkException -> {
                        _markersState.value = MarkersState.Error(
                            snackbarMessage = exception.message!!,
                            networkAvailable = false,
                        )
                    }

                    else -> {
                        _markersState.value = MarkersState.Error(
                            snackbarMessage = exception.message!!,
                        )
                    }
                }
            }
        }
    }

    private var searchJob: Job? = null

    fun searchMarkers(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500L)
        }
    }

    private val _temtemLayerState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val temtemLayerState: StateFlow<Boolean> = _temtemLayerState.asStateFlow()

    fun changeTemtemLayerVisibility() {
        _temtemLayerState.value = !_temtemLayerState.value

        changeLayerVisibility(_temtemLayerState.value, listOf(Marker.Type.Spawn))
    }

    private val _landmarkLayerState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val landmarkLayerState: StateFlow<Boolean> = _landmarkLayerState.asStateFlow()

    fun changeLandmarkLayerVisibility() {
        _landmarkLayerState.value = !_landmarkLayerState.value

        changeLayerVisibility(_landmarkLayerState.value, listOf(Marker.Type.Saipark))
    }

    private fun changeLayerVisibility(visible: Boolean, types: List<Marker.Type>) {
        val markersState = _markersState.value as MarkersState.Success

        if (visible) {
            val oldMarkers = markersState.oldMarkers.toMutableList()
            val newMarkers = oldMarkers.filter { it.type in types }

            oldMarkers.removeAll(newMarkers)

            _markersState.value = MarkersState.Success(
                markersState.newMarkers + newMarkers,
                oldMarkers,
            )
        } else {
            val newMarkers = markersState.newMarkers.toMutableList()
            val oldMarkers = newMarkers.filter { it.type in types }

            newMarkers.removeAll(oldMarkers)

            _markersState.value = MarkersState.Success(
                newMarkers,
                markersState.oldMarkers + oldMarkers,
            )
        }
    }

    fun isUserSignedIn(): Boolean {
        return authRepository.isUserSignedIn()
    }

    fun signOut() {
        _markersState.value = MarkersState.Empty
        authRepository.signOut()
    }
}
