package com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.ImageDto

data class EvolutionDto(
    val name: String,
    val traits: List<String>,
    val condition: String,
    val image: ImageDto,
)
