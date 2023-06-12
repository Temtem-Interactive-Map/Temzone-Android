package com.temtem.interactive.map.temzone.domain.repository.temzone

import com.temtem.interactive.map.temzone.domain.repository.temzone.model.Page
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.MarkerSaipark
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.MarkerSpawn

interface TemzoneRepository {
    suspend fun getMarkers(): List<Marker>
    suspend fun getMarkerSpawn(id: String): MarkerSpawn
    suspend fun setTemtemObtained(id: Int)
    suspend fun getMarkerSaipark(id: String): MarkerSaipark
    suspend fun searchMarkers(query: String, limit: Int, offset: Int): Page<Marker>
}
