package com.temtem.interactive.map.temzone.repositories.temzone.retrofit

import com.temtem.interactive.map.temzone.repositories.temzone.data.Marker
import com.temtem.interactive.map.temzone.repositories.temzone.data.Page
import retrofit2.http.GET
import retrofit2.http.Query

interface TemzoneApi {

    @GET("user/markers")
    suspend fun getMarkers(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): Page<Marker>
}
