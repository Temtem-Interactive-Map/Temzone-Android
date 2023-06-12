package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.TraitDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.temtem.Trait

fun TraitDto.toTrait(): Trait {
    return Trait(
        name = name,
        description = description,
    )
}
