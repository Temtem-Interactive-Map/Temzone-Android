package com.temtem.interactive.map.temzone.domain.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.TypeDto
import com.temtem.interactive.map.temzone.domain.model.temtem.Type

fun TypeDto.toType(): Type {
    return Type(
        name = name,
        imageUrl = image.url,
    )
}
