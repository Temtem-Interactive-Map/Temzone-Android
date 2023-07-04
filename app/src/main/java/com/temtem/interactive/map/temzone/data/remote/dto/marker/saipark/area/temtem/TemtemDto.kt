package com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark.area.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.ImageDto
import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem.TypeDto

data class TemtemDto(
    val id: Int,
    val name: String,
    val types: List<TypeDto>,
    val image: ImageDto,
)
