package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem.EvolutionDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Evolution

fun EvolutionDto.toEvolution(): Evolution {
    return Evolution(
        name = name,
        traits = traits,
        condition = condition,
        imageUrl = image.url,
    )
}
