package com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn

import com.temtem.interactive.map.temzone.data.remote.dto.ImageDto
import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem.TemtemDto

data class SpawnDto(
    val id: String,
    val rate: List<Int>,
    val level: LevelDto,
    val condition: String?,
    val temtem: TemtemDto,
    val image: ImageDto,
)
