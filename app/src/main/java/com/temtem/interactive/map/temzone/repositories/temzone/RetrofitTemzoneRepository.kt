package com.temtem.interactive.map.temzone.repositories.temzone

import com.temtem.interactive.map.temzone.repositories.temzone.data.Marker
import com.temtem.interactive.map.temzone.repositories.temzone.retrofit.TemzoneApi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RetrofitTemzoneRepository @Inject constructor(
    private val temzoneApi: TemzoneApi,
) : TemzoneRepository {

    override suspend fun getMarkers(): List<Marker> {
        try {
            val markers = mutableListOf<Deferred<List<Marker>>>()

            coroutineScope {
                for (offset in 0..600 step 200) {
                    val items = async {
                        temzoneApi.getMarkers(200, offset).items
                    }

                    markers.add(items)
                }
            }

            return markers.awaitAll().flatten()
        } catch (e: Exception) {
            throw e
        }
    }
}
