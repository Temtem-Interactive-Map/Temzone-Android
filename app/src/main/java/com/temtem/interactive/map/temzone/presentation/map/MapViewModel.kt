package com.temtem.interactive.map.temzone.presentation.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.temtem.interactive.map.temzone.R
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
    private val application: Application,
    private val authRepository: AuthRepository,
    private val temzoneRepository: TemzoneRepository,
    private val networkRepository: NetworkRepository,
) : ViewModel() {

    private val _mapState: MutableStateFlow<MapState> = MutableStateFlow(MapState.Empty)
    val mapState: SharedFlow<MapState> = _mapState.asSharedFlow()

    init {
        viewModelScope.launch {
            networkRepository.getStatus().collect {
                if (it == NetworkStatus.AVAILABLE &&
                    _mapState.value is MapState.Error &&
                    (_mapState.value as MapState.Error).snackbarMessage == application.getString(R.string.network_error)
                ) {
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
                    MapState.Success(markers)
                }
            } catch (exception: Exception) {
                _mapState.update {
                    MapState.Error(exception.message.orEmpty())
                }
            }
        }
    }

    fun search(query: String): Flow<PagingData<Marker>> {
        return temzoneRepository.search(query).cachedIn(viewModelScope)
    }

    private val _temtemLayerState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val temtemLayerState: StateFlow<Boolean> = _temtemLayerState.asStateFlow()

    fun changeTemtemLayerVisibility(force: Boolean = false) {
        _temtemLayerState.update { force || !it }

        changeLayerVisibility(_temtemLayerState.value, listOf(Marker.Type.Spawn))
    }

    private val _landmarkLayerState: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val landmarkLayerState: StateFlow<Boolean> = _landmarkLayerState.asStateFlow()

    fun changeLandmarkLayerVisibility(force: Boolean = false) {
        _landmarkLayerState.update { force || !it }

        changeLayerVisibility(_landmarkLayerState.value, listOf(Marker.Type.Saipark))
    }

    private fun changeLayerVisibility(visible: Boolean, types: List<Marker.Type>) {
        val mapState = if (_mapState.value is MapState.Success) MapState.Update(
            (_mapState.value as MapState.Success).markers,
            emptyList(),
        )
        else _mapState.value as MapState.Update

        if (visible) {
            val oldMarkers = mapState.oldMarkers.toMutableList()
            val newMarkers = oldMarkers.filter { it.type in types }

            oldMarkers.removeAll(newMarkers)

            _mapState.update {
                MapState.Update(
                    mapState.newMarkers + newMarkers,
                    oldMarkers,
                )
            }
        } else {
            val newMarkers = mapState.newMarkers.toMutableList()
            val oldMarkers = newMarkers.filter { it.type in types }

            newMarkers.removeAll(oldMarkers)

            _mapState.update {
                MapState.Update(
                    newMarkers,
                    mapState.oldMarkers + oldMarkers,
                )
            }
        }
    }

    fun setTemtemObtained(title: String) {
        val mapState = if (_mapState.value is MapState.Success) MapState.Update(
            (_mapState.value as MapState.Success).markers,
            emptyList(),
        )
        else _mapState.value as MapState.Update

        _mapState.update {
            MapState.Update(
                mapState.newMarkers.map {
                    if (
                        it.title.split(" ").first() == title.split(" ").first()
                    ) it.copy(obtained = !it.obtained) else it
                },
                mapState.oldMarkers.map {
                    if (
                        it.title.split(" ").first() == title.split(" ").first()
                    ) it.copy(obtained = !it.obtained) else it
                },
            )
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
        _temtemLayerState.update { true }
        _landmarkLayerState.update { true }

        viewModelScope.launch {
            authRepository.signOut()
            _signOutState.emit(true)
        }
    }
}
