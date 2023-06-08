package com.temtem.interactive.map.temzone.data.remote

import com.temtem.interactive.map.temzone.data.remote.dto.PageDto
import com.temtem.interactive.map.temzone.data.remote.dto.marker.MarkerDto
import com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark.MarkerSaiparkDto
import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.MarkerSpawnDto
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TemzoneApi {
    @AUTHENTICATED
    @GET("users/markers")
    suspend fun getMarkers(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): PageDto<MarkerDto>

    @AUTHENTICATED
    @GET("markers/spawns/{id}")
    suspend fun getMarkerSpawn(
        @Path("id") id: String,
    ): MarkerSpawnDto

    @AUTHENTICATED
    @PUT("users/temtem/{id}")
    suspend fun setTemtemObtained(
        @Path("id") id: Int,
    )

    @AUTHENTICATED
    @GET("markers/saipark/{id}")
    suspend fun getMarkerSaipark(
        @Path("id") id: String,
    ): MarkerSaiparkDto

    @AUTHENTICATED
    @GET("search")
    suspend fun searchMarkers(
        @Query("query") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): PageDto<MarkerDto>
}
