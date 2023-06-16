package com.temtem.interactive.map.temzone.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.temtem.interactive.map.temzone.domain.exception.NetworkException
import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepository
import com.temtem.interactive.map.temzone.domain.repository.network.NetworkRepository
import com.temtem.interactive.map.temzone.domain.repository.network.model.NetworkStatus
import com.temtem.interactive.map.temzone.domain.repository.temzone.TemzoneRepository
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker
import com.temtem.interactive.map.temzone.presentation.map.state.MapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val temzoneRepository: TemzoneRepository,
    private val networkRepository: NetworkRepository,
) : ViewModel() {

    private val _mapState: MutableStateFlow<MapState> = MutableStateFlow(MapState.Empty)
    val mapState: SharedFlow<MapState> = _mapState.asSharedFlow()

    init {
        viewModelScope.launch {
            networkRepository.getStatus().collect {
                if (it == NetworkStatus.AVAILABLE && _mapState.value is MapState.Error && !(_mapState.value as MapState.Error).networkAvailable) {
                    getMarkers()
                }
            }
        }
    }

    fun getMarkers() {
        _mapState.update {
            MapState.Loading
        }

        viewModelScope.launch {
            try {
                val markers = temzoneRepository.getMarkers()

                _mapState.update {
                    MapState.Success(markers, emptyList())
                }
            } catch (exception: Exception) {
                when (exception) {
                    is NetworkException -> {
                        _mapState.update {
                            MapState.Error(
                                snackbarMessage = exception.message.orEmpty(),
                                networkAvailable = false,
                            )
                        }
                    }

                    else -> {
                        _mapState.update {
                            MapState.Error(
                                snackbarMessage = exception.message.orEmpty(),
                            )
                        }
                    }
                }
            }
        }
    }

    fun searchMarkers(query: String): Flow<PagingData<Marker>> {
        return temzoneRepository.searchMarkers(query).cachedIn(viewModelScope)
    }

    private val _temtemLayerState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val temtemLayerState: StateFlow<Boolean> = _temtemLayerState.asStateFlow()

    fun changeTemtemLayerVisibility() {
        _temtemLayerState.update { !it }

        changeLayerVisibility(_temtemLayerState.value, listOf(Marker.Type.Spawn))
    }

    private val _landmarkLayerState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val landmarkLayerState: StateFlow<Boolean> = _landmarkLayerState.asStateFlow()

    fun changeLandmarkLayerVisibility() {
        _landmarkLayerState.update { !it }

        changeLayerVisibility(_landmarkLayerState.value, listOf(Marker.Type.Saipark))
    }

    private fun changeLayerVisibility(visible: Boolean, types: List<Marker.Type>) {
        val mapState = _mapState.value as MapState.Success

        if (visible) {
            val oldMarkers = mapState.oldMarkers.toMutableList()
            val newMarkers = oldMarkers.filter { it.type in types }

            oldMarkers.removeAll(newMarkers)

            _mapState.update {
                MapState.Success(
                    mapState.newMarkers + newMarkers,
                    oldMarkers,
                )
            }
        } else {
            val newMarkers = mapState.newMarkers.toMutableList()
            val oldMarkers = newMarkers.filter { it.type in types }

            newMarkers.removeAll(oldMarkers)

            _mapState.update {
                MapState.Success(
                    newMarkers,
                    mapState.oldMarkers + oldMarkers,
                )
            }
        }
    }

    fun isUserSignedIn(): Boolean {
        return authRepository.isUserSignedIn()
    }

    private val _signOutState = MutableSharedFlow<Boolean>()
    val signOutState: SharedFlow<Boolean> = _signOutState.asSharedFlow()

    fun signOut() {
        _mapState.update {
            MapState.Empty
        }

        viewModelScope.launch {
            authRepository.signOut()
            _signOutState.emit(true)
        }
    }
}
