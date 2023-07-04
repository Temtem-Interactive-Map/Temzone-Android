package com.temtem.interactive.map.temzone.domain.repository.temzone

import androidx.paging.PagingData
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.Saipark
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.Spawn
import kotlinx.coroutines.flow.Flow

interface TemzoneRepository {
    suspend fun getMarkers(): List<Marker>
    suspend fun getSpawn(id: String): Spawn
    suspend fun getSaipark(id: String): Saipark
    suspend fun setTemtemObtained(id: Int)
    fun search(query: String): Flow<PagingData<Marker>>
}
