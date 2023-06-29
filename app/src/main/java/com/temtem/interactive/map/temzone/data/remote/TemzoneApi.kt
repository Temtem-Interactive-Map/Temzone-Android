package com.temtem.interactive.map.temzone.data.remote

import com.temtem.interactive.map.temzone.data.remote.annotation.AUTHENTICATED
import com.temtem.interactive.map.temzone.data.remote.dto.PageDto
import com.temtem.interactive.map.temzone.data.remote.dto.marker.MarkerDto
import com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark.SaiparkDto
import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.SpawnDto
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
    suspend fun getSpawn(
        @Path("id") id: String,
    ): SpawnDto

    @AUTHENTICATED
    @GET("markers/saipark/{id}")
    suspend fun getSaipark(
        @Path("id") id: String,
    ): SaiparkDto

    @AUTHENTICATED
    @PUT("users/temtem/{id}")
    suspend fun setTemtemObtained(
        @Path("id") id: Int,
    )

    @AUTHENTICATED
    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): PageDto<MarkerDto>
}
