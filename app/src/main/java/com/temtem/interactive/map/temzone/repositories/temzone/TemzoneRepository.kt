package com.temtem.interactive.map.temzone.repositories.temzone

import com.temtem.interactive.map.temzone.repositories.temzone.data.Marker

interface TemzoneRepository {
    suspend fun getMarkers(): List<Marker>
}
