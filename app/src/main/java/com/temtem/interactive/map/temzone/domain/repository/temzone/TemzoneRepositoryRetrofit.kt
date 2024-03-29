package com.temtem.interactive.map.temzone.domain.repository.temzone

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.temtem.interactive.map.temzone.data.remote.TemzoneApi
import com.temtem.interactive.map.temzone.data.remote.dto.marker.MarkerDto
import com.temtem.interactive.map.temzone.domain.exception.NetworkException
import com.temtem.interactive.map.temzone.domain.exception.UnknownException
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.saipark.toMarkerSaipark
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.toMarkerSpawn
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.toMarker
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.Saipark
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.Spawn
import com.temtem.interactive.map.temzone.domain.repository.temzone.paging.MarkerPagingSource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class TemzoneRepositoryRetrofit @Inject constructor(
    private val application: Application,
    private val temzoneApi: TemzoneApi,
) : TemzoneRepository {

    override suspend fun getMarkers(): List<Marker> {
        try {
            val markers = mutableListOf<Deferred<List<MarkerDto>>>()

            coroutineScope {
                for (offset in 0..400 step 200) {
                    val items = async {
                        val page = temzoneApi.getMarkers(200, offset)

                        page.items
                    }

                    markers.add(items)
                }
            }
            return markers.awaitAll().flatten().map {
                it.toMarker()
            }
        } catch (exception: Exception) {
            when (exception) {
                is IOException -> {
                    throw NetworkException(application)
                }

                else -> {
                    throw UnknownException(application)
                }
            }
        }
    }

    override suspend fun getSpawn(id: String): Spawn {
        try {
            return temzoneApi.getSpawn(id).toMarkerSpawn()
        } catch (exception: Exception) {
            when (exception) {
                is IOException -> {
                    throw NetworkException(application)
                }

                else -> {
                    throw UnknownException(application)
                }
            }
        }
    }

    override suspend fun getSaipark(id: String): Saipark {
        try {
            return temzoneApi.getSaipark(id).toMarkerSaipark()
        } catch (exception: Exception) {
            when (exception) {
                is IOException -> {
                    throw NetworkException(application)
                }

                else -> {
                    throw UnknownException(application)
                }
            }
        }
    }

    override suspend fun setTemtemObtained(id: Int) {
        try {
            temzoneApi.setTemtemObtained(id)
        } catch (exception: Exception) {
            when (exception) {
                is IOException -> {
                    throw NetworkException(application)
                }

                else -> {
                    throw UnknownException(application)
                }
            }
        }
    }

    override fun search(query: String): Flow<PagingData<Marker>> {
        return Pager(PagingConfig(20)) {
            MarkerPagingSource(query, application, temzoneApi)
        }.flow
    }
}
