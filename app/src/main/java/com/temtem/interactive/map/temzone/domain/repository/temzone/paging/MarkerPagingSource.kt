package com.temtem.interactive.map.temzone.domain.repository.temzone.paging

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.temtem.interactive.map.temzone.data.remote.TemzoneApi
import com.temtem.interactive.map.temzone.domain.exception.NetworkException
import com.temtem.interactive.map.temzone.domain.exception.UnknownException
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.toMarker
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker
import java.io.IOException

class MarkerPagingSource(
    private val query: String,
    private val context: Context,
    private val temzoneApi: TemzoneApi,
) : PagingSource<Int, Marker>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Marker> {
        return try {
            if (query.isBlank()) {
                LoadResult.Page(emptyList(), null, null)
            } else {
                val page = temzoneApi.search(query, params.loadSize, params.key ?: 0)
                val items = page.items.map { it.toMarker() }

                LoadResult.Page(items, null, page.next)
            }
        } catch (exception: Exception) {
            when (exception) {
                is IOException -> {
                    LoadResult.Error(NetworkException(context))
                }

                else -> {
                    LoadResult.Error(UnknownException(context))
                }
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Marker>): Int = 0
}
