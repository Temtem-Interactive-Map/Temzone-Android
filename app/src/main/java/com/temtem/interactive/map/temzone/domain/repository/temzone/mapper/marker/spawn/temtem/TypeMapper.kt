package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem.TypeDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Type

fun TypeDto.toType(): Type {
    return Type(
        name = name,
        imageUrl = image.url,
    )
}
