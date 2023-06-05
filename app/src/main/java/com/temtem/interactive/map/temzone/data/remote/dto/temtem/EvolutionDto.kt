package com.temtem.interactive.map.temzone.data.remote.dto.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.ImageDto

data class EvolutionDto(
    val name: String,
    val traits: List<TraitDto>,
    val condition: String,
    val image: ImageDto,
)
