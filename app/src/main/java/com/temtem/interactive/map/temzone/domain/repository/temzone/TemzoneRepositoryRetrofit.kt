package com.temtem.interactive.map.temzone.domain.repository.temzone

import android.app.Application
import com.temtem.interactive.map.temzone.data.remote.TemzoneApi
import com.temtem.interactive.map.temzone.data.remote.dto.marker.MarkerDto
import com.temtem.interactive.map.temzone.domain.exception.NetworkException
import com.temtem.interactive.map.temzone.domain.exception.UnknownException
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.saipark.toMarkerSaipark
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.toMarkerSpawn
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.toMarker
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.toPage
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.Page
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.MarkerSaipark
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.MarkerSpawn
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    override suspend fun getMarkerSpawn(id: String): MarkerSpawn {
        try {
            return temzoneApi.getMarkerSpawn(id).toMarkerSpawn()
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

    override suspend fun getMarkerSaipark(id: String): MarkerSaipark {
        try {
            return temzoneApi.getMarkerSaipark(id).toMarkerSaipark()
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

    override suspend fun searchMarkers(query: String, limit: Int, offset: Int): Page<Marker> {
        try {
            return temzoneApi.searchMarkers(query, limit, offset).let { page ->
                page.toPage(page.items.map {
                    it.toMarker()
                })
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
}
