package com.temtem.interactive.map.temzone.repositories.temzone

import com.temtem.interactive.map.temzone.exceptions.InternalException
import com.temtem.interactive.map.temzone.exceptions.NetworkException
import com.temtem.interactive.map.temzone.repositories.temzone.data.Marker
import com.temtem.interactive.map.temzone.repositories.temzone.retrofit.TemzoneApi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.IOException
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
        } catch (exception: Exception) {
            when (exception) {
                is IOException -> {
                    throw NetworkException(exception)
                }

                else -> {
                    throw InternalException(exception)
                }
            }
        }
    }
}
