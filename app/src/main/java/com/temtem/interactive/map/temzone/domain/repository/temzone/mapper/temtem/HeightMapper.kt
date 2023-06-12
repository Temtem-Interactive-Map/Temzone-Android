package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.HeightDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.temtem.Height

fun HeightDto.toHeight(): Height {
    return Height(
        cm = cm,
        inches = inches,
    )
}
