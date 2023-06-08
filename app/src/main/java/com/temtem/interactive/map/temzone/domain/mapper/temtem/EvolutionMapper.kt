package com.temtem.interactive.map.temzone.domain.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.EvolutionDto
import com.temtem.interactive.map.temzone.domain.model.temtem.Evolution

fun EvolutionDto.toEvolution(): Evolution {
    return Evolution(
        name = name,
        traits = traits.map { it.toTrait() },
        condition = condition,
        imageUrl = image.url,
    )
}
