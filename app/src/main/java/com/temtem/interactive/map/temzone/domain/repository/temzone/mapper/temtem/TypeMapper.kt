package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.TypeDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.temtem.Type

fun TypeDto.toType(): Type {
    return Type(
        name = name,
        imageUrl = image.url,
    )
}
