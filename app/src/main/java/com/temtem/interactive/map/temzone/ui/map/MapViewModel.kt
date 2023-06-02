package com.temtem.interactive.map.temzone.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temtem.interactive.map.temzone.repositories.temzone.TemzoneRepository
import com.temtem.interactive.map.temzone.repositories.temzone.data.Marker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val temzoneRepository: TemzoneRepository,
) : ViewModel() {

    private val _markers = MutableStateFlow(listOf<Marker>())
    val markers = _markers.asStateFlow()

    init {
        viewModelScope.launch {
            _markers.value = temzoneRepository.getMarkers()
        }
    }

    fun searchMarkers(query: String) {
    }
}
