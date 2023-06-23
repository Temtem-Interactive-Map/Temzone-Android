package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem.HeightDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Height

fun HeightDto.toHeight(): Height {
    return Height(
        cm = cm,
        inches = inches,
    )
}
