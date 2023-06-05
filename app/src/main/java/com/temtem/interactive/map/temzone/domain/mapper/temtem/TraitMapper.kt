package com.temtem.interactive.map.temzone.domain.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.TraitDto
import com.temtem.interactive.map.temzone.domain.model.temtem.Trait

fun TraitDto.toTrait(): Trait {
    return Trait(
        name = name,
        description = description,
    )
}
