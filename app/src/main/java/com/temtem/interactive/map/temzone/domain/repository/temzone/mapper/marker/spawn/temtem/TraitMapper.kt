package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem.TraitDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Trait

fun TraitDto.toTrait(): Trait {
    return Trait(
        name = name,
        description = description,
    )
}
