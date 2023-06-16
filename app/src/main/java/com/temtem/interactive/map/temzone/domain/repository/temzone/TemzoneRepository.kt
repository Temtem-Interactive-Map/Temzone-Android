package com.temtem.interactive.map.temzone.domain.repository.temzone

import androidx.paging.PagingData
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.MarkerSaipark
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.MarkerSpawn
import kotlinx.coroutines.flow.Flow

interface TemzoneRepository {
    suspend fun getMarkers(): List<Marker>
    suspend fun getMarkerSpawn(id: String): MarkerSpawn
    suspend fun setTemtemObtained(id: Int)
    suspend fun getMarkerSaipark(id: String): MarkerSaipark
    fun searchMarkers(query: String): Flow<PagingData<Marker>>
}
