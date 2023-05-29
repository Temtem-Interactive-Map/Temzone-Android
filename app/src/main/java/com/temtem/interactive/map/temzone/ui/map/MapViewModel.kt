package com.temtem.interactive.map.temzone.ui.map

import androidx.lifecycle.ViewModel
import com.temtem.interactive.map.temzone.data.Coordinates
import com.temtem.interactive.map.temzone.data.Marker
import com.temtem.interactive.map.temzone.data.MarkerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {

    private val _markers = MutableStateFlow(listOf<Marker>())
    val markers = _markers.asStateFlow()

    init {
        _markers.value = listOf(
            Marker(
                "id1", MarkerType.SAIPARK, "title", "subtitle", Coordinates(11039.0, 6692.0), false
            ), Marker(
                "id2", MarkerType.SPAWN, "Mimit", "subtitle", Coordinates(11039.0 / 2, 6692.0), true
            ), Marker(
                "id3",
                MarkerType.SPAWN,
                "Mimit",
                "subtitle",
                Coordinates(11039.0 / 2 - 100, 6692.0),
                false
            )
        )
    }

    fun onSearchQueryChanged(query: String) {
    }
}
