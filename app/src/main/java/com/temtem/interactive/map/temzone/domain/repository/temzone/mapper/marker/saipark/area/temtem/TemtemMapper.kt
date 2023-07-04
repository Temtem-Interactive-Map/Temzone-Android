package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.saipark.area.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark.area.temtem.TemtemDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.temtem.toType
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.area.temtem.Temtem

fun TemtemDto.toTemtem(): Temtem {
    return Temtem(
        id = id,
        name = name,
        types = types.map { it.toType() },
        imageUrl = image.url,
    )
}